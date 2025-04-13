package com.adam.buru.timemanager.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adam.buru.timemanager.ui.screen.HomeScreen
import com.adam.buru.timemanager.ui.screen.LoginScreen
import com.adam.buru.timemanager.ui.screen.PomodoroScreen
import com.adam.buru.timemanager.ui.screen.ProfileScreen
import com.adam.buru.timemanager.ui.screen.RegisterScreen
import com.adam.buru.timemanager.ui.screen.TaskAddScreen
import com.adam.buru.timemanager.ui.screen.TaskDetailScreen
import com.adam.buru.timemanager.ui.screen.TaskEditScreen
import com.adam.buru.timemanager.ui.screen.TaskScreen

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route
    ) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController)
        }
        composable(route = Screen.RegisterScreen.route) {
            RegisterScreen(navController)
        }
        composable(route = Screen.HomeScreen.route) {
            val homeNavController = rememberNavController()
            Scaffold(
                bottomBar = { BottomNavigationBar(homeNavController) }
            ) { homePadding ->
                NavHost(
                    navController = homeNavController,
                    startDestination = Screen.HomeScreen.route,
                    modifier = Modifier.padding(homePadding)
                ) {
                    composable(route = Screen.HomeScreen.route) {
                        HomeScreen()
                    }
                    composable(route = Screen.TaskScreen.route) {
                        TaskScreen(homeNavController)
                    }
                    composable(route = Screen.TaskAddScreen.route) {
                        TaskAddScreen(homeNavController)
                    }
                    composable("${Screen.TaskEditScreen.route}/{taskId}") { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getString("taskId")?.toInt() ?: 0
                        TaskEditScreen(homeNavController, taskId)
                    }
                    composable(route = "${Screen.TaskDetailScreen.route}/{taskId}") { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getString("taskId")?.toInt() ?: 0
                        TaskDetailScreen(homeNavController, taskId)
                    }
                    composable(route = Screen.PomodoroScreen.route) {
                        PomodoroScreen()
                    }
                    composable(route = Screen.ProfileScreen.route) {
                        ProfileScreen(navController)
                    }
                }
            }
        }
    }
}