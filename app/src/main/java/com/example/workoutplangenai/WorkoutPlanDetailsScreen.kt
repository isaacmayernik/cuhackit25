package com.example.workoutplangenai

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun WorkoutPlanDetailsScreen(navController: NavHostController, planId: String) {
    val context = LocalContext.current
    val plan = remember { getWorkoutPlanById(context, planId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = "Workout Plan Details",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = plan?.content ?: "Plan not found",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                deleteWorkoutPlan(context, planId)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Delete Plan")
        }
    }
}

fun getWorkoutPlanById(context: Context, id: String): WorkoutPlan? {
    val sharedPreferences = context.getSharedPreferences("workout_plans", Context.MODE_PRIVATE)
    val content = sharedPreferences.getString(id, null)
    return if (content != null) WorkoutPlan(id, content) else null
}

fun deleteWorkoutPlan(context: Context, id: String) {
    val sharedPreferences = context.getSharedPreferences("workout_plans", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        remove(id)
        apply()
    }
}