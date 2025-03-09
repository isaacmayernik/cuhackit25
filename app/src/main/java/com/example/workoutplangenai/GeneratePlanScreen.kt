package com.example.workoutplangenai

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.util.concurrent.TimeUnit

suspend fun generateWorkoutPlan(prompt: String): WorkoutPlan {
    return withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            val mediaType = "application/json".toMediaType()
            val jsonBody = """
            {
                "prompt": "$prompt"
            }
            """.trimIndent()

            val requestBody = jsonBody.toRequestBody(mediaType)
            val request = Request.Builder()
                .url("http://10.0.2.2:5000/generate-workout-plan")
                .post(requestBody)
                .build()

            val response: Response = client.newCall(request).execute()
            response.use {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val generatedText = responseBody?.substringAfter("\"workout_plan\":\"")?.substringBefore("\"")
                        ?: "Failed to generate workout plan."
                    WorkoutPlan(System.currentTimeMillis().toString(), generatedText.replace("\\n", "\n"))
                } else {
                    val errorMessage = "Error: ${response.code} - ${response.message}"
                    WorkoutPlan(System.currentTimeMillis().toString(), errorMessage)
                }
            }
        } catch (e: Exception) {
            WorkoutPlan(System.currentTimeMillis().toString(), "Error: ${e.message}")
        }
    }
}

@Composable
fun GeneratePlan(navController: NavHostController) {
    val context = LocalContext.current
    var workoutPlans by remember { mutableStateOf(getSavedWorkoutPlans(context)) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Generated Workout Plans",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(workoutPlans) { plan ->
                    Button(
                        onClick = {
                            navController.navigate("workout_plan_detail/${plan.id}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(formatPlanById(plan.id))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    val userInfo = getUserInfo(context)
                    val prompt = generatePrompt(userInfo)
                    val generatedPlan = generateWorkoutPlan(prompt)

                    if (generatedPlan.content.isNotEmpty()) {
                        saveWorkoutPlan(context, generatedPlan)
                        workoutPlans = getSavedWorkoutPlans(context)
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("Generate Plan")
        }
    }
}

fun generatePrompt(userInfo: UserInfo): String {
    return """
        Generate a workout plan for a user with the following details:
        - Weight: ${userInfo.weight} lbs
        - Height: ${userInfo.height} inches
        - Activity level: ${userInfo.activityLevel}
        - Goal: ${userInfo.goal}
        - Dietary Preferences: ${userInfo.dietaryPreferences.joinToString(", ")}
    """
        .trimIndent()
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
}

fun saveWorkoutPlan(context: Context, plan: WorkoutPlan) {
    val sharedPreferences = context.getSharedPreferences("workout_plans", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString(plan.id, plan.content)
        apply()
    }
}

fun getSavedWorkoutPlans(context: Context): List<WorkoutPlan> {
    val sharedPreferences = context.getSharedPreferences("workout_plans", Context.MODE_PRIVATE)
    return sharedPreferences.all.map { (id, content) ->
        WorkoutPlan(id, content.toString())
    }
}

fun formatPlanById(id: String): String {
    return try {
        val timestamp = id.toLong()
        val date = java.util.Date(timestamp)
        val formatter = java.text.SimpleDateFormat("MM-dd-yyyy HH:mm:ss", java.util.Locale.getDefault())
        "${formatter.format(date)} $id"
    } catch (e: Exception) {
        "Invalid Plan ID"
    }
}

data class WorkoutPlan(val id: String, val content: String)