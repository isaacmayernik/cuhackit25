package com.example.workoutplangenai

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun WelcomeScreen(navController: NavHostController, isFirstTime: MutableState<Boolean>) {
    val context = LocalContext.current
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var dietaryPreferences by remember { mutableStateOf(setOf<String>()) }

    var showActivityLevelDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Weight input
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (lbs) ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Height input
        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Height (inches) ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Activity level button
        Button(
            onClick = { showActivityLevelDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (activityLevel.isEmpty()) "Select Activity Level" else "Activity Level: $activityLevel")
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Goal button
        Button(
            onClick = { showGoalDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (goal.isEmpty()) "Select Goal" else "Goal: $goal")
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Dietary Preferences
        Text("Dietary Preferences:", style = MaterialTheme.typography.bodyLarge)
        val options = listOf("Normal", "No gluten", "Vegan", "Pescatarian")
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = dietaryPreferences.contains(option),
                        onClick = {
                            dietaryPreferences = if (option == "Normal") {
                                setOf("Normal")
                            } else {
                                (dietaryPreferences - "Normal") + option
                            }
                        }
                    )
                    .padding(8.dp)
            ) {
                Checkbox(
                    checked = dietaryPreferences.contains(option),
                    onCheckedChange = { checked ->
                        dietaryPreferences = if (option == "Normal") {
                            if (checked) setOf("Normal") else emptySet()
                        } else {
                            if (checked) (dietaryPreferences - "Normal") + option else dietaryPreferences - option
                        }
                    }
                )
                Text(option, modifier = Modifier.padding(start = 8.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Save
        Button(
            onClick = {
                saveUserInfo(context, weight, height, activityLevel, goal, dietaryPreferences)
                isFirstTime.value = false
                navController.navigate(Screen.Main.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }

    if (showActivityLevelDialog) {
        AlertDialog(
            onDismissRequest = { showActivityLevelDialog = false },
            title = { Text("Select Activity Level") },
            text = {
                Column {
                    listOf("Very Active", "Active", "Not Active").forEach { level ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = activityLevel == level,
                                    onClick = {
                                        activityLevel = level
                                        showActivityLevelDialog = false
                                    }
                                )
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = activityLevel == level,
                                onClick = {
                                    activityLevel = level
                                    showActivityLevelDialog = false
                                }
                            )
                            Text(level, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = { }
        )
    }

    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("Select Goal") },
            text = {
                Column {
                    listOf("Lose Weight", "Maintain Weight", "Gain Weight").forEach { _goal ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = goal == _goal,
                                    onClick = {
                                        goal = _goal  // Fix: Set goal, not activityLevel
                                        showGoalDialog = false
                                    }
                                )
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = goal == _goal,
                                onClick = {
                                    goal = _goal  // Fix: Set goal, not activityLevel
                                    showGoalDialog = false
                                }
                            )
                            Text(_goal, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = { }
        )
    }
}

fun saveUserInfo(
    context: Context,
    weight: String,
    height: String,
    activityLevel: String,
    goal: String,
    dietaryPreferences: Set<String>
) {
    val sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("weight", weight)
        putString("height", height)
        putString("activity_level", activityLevel)
        putString("goal", goal)
        putStringSet("dietary_preferences", dietaryPreferences)
        apply()
    }
}

fun getUserInfo(context: Context): UserInfo {
    val sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE)
    return UserInfo(
        weight = sharedPreferences.getString("weight", null),
        height = sharedPreferences.getString("height", null),
        activityLevel = sharedPreferences.getString("activity_level", null),
        goal = sharedPreferences.getString("goal", null),
        dietaryPreferences = sharedPreferences.getStringSet("dietary_preferences", emptySet()) ?: emptySet()
    )
}

data class UserInfo(
    val weight: String?,
    val height: String?,
    val activityLevel: String?,
    val goal: String?,
    val dietaryPreferences: Set<String>
)