package com.adam.buru.timemanager.navigation

sealed class Screen(val route: String) {
    data object HomeScreen : Screen("home_route")

    data object TaskScreen : Screen("task_route")
    data object TaskAddScreen : Screen("addTask_route")
    data object TaskEditScreen : Screen("editTask_route")
    data object TaskDetailScreen : Screen("taskDetail_route")

    data object PomodoroScreen : Screen("pomodoro_route")
    data object ProfileScreen : Screen("profile_route")
    data object LoginScreen : Screen("login_route")
    data object RegisterScreen : Screen("register_route")
}