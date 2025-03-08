package com.example.workoutplangenai

import android.content.Context
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun GeneratePlan(navController: NavHostController) {
    val context = LocalContext.current
    var workoutPlans by remember { mutableStateOf(getSavedWorkoutPlans(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                            Text("Workout Plan ${plan.id}")
                }
            }
        }

        FloatingActionButton(
            onClick = {
                val userInfo = getUserInfo(context)
                val prompt = generatePrompt(userInfo)
                val generatedPlan = generateWorkoutPlan(prompt)

                saveWorkoutPlan(context, generatedPlan)
                workoutPlans = getSavedWorkoutPlans(context)
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End)
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
    """.trimIndent()
}
fun generateWorkoutPlan(prompt: String): WorkoutPlan {
    val generatedContent = "Generated workout plan based on $prompt"
    return WorkoutPlan(System.currentTimeMillis().toString(), generatedContent)
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

data class WorkoutPlan(val id: String, val content: String)