package com.example.workoutplangenai

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.workoutplangenai.ui.theme.WorkoutPlanGenAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkoutPlanGenAITheme {
                Navigation()
            }
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE)
    val isFirstTime = sharedPreferences.getString("weight", null) == null

    Scaffold(
        bottomBar = {
            if (!isFirstTime) {
                BottomBarNavigation(navController)
            }
        }
    ) { innerPadding ->
        NavigationGraph(navController, Modifier.padding(innerPadding), isFirstTime)
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Main : Screen("home", "Main Screen", Icons.Default.Home)
    data object Welcome : Screen("welcome", "Welcome Screen", Icons.Default.Star)
}

@Composable
fun BottomBarNavigation(navController: NavHostController) {
    val items = listOf (
        Screen.Main,
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                            inclusive = false
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) }
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier, isFirstTime: Boolean) {
    NavHost(
        navController = navController,
        startDestination = if (isFirstTime) Screen.Welcome.route else Screen.Main.route,
        modifier = modifier,
    ) {
        composable(Screen.Welcome.route) { WelcomeScreen(navController) }
        composable(Screen.Main.route) { MainScreen(navController) }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    Main(navController)
}