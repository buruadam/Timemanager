package com.adam.buru.timemanager.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adam.buru.timemanager.navigation.Screen
import com.adam.buru.timemanager.ui.viewmodel.TaskOperationState
import com.adam.buru.timemanager.ui.viewmodel.TaskUIState
import com.adam.buru.timemanager.ui.viewmodel.TaskViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    navController: NavController,
    taskId: Int,
    taskViewModel: TaskViewModel = koinViewModel()
) {

    val context = LocalContext.current
    val taskState by taskViewModel.taskUIState.collectAsState()
    val taskOperationState by taskViewModel.taskOperationState.collectAsState()

    LaunchedEffect(taskId) {
        taskViewModel.getTaskById(taskId, context)
    }

    LaunchedEffect(taskOperationState) {
        when (taskOperationState) {
            is TaskOperationState.Success -> {
                val successMessage = (taskOperationState as TaskOperationState.Success).message
                Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.TaskScreen.route) {
                    popUpTo(Screen.HomeScreen.route) { inclusive = true }
                }
            }
            is TaskOperationState.Error -> {
                val errorMessage = (taskOperationState as TaskOperationState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        taskViewModel.deleteTask(taskId, context)
                        navController.navigate(Screen.TaskScreen.route) {
                            popUpTo(Screen.TaskScreen.route) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Edit")
                    }
                    IconButton(onClick = {
                        navController.navigate("${Screen.TaskEditScreen.route}/$taskId")
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { contentPadding ->
        when(taskState) {
            is TaskUIState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is TaskUIState.Success -> {
                val tasks = (taskState as TaskUIState.Success).task
                val task = tasks.first()
                val formattedDate = LocalDateTime
                    .parse(task.dueDate)
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                Column(modifier = Modifier.padding(contentPadding).padding(16.dp)) {
                    Text(text = task.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = task.description, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Due Date: $formattedDate", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            is TaskUIState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = (taskState as TaskUIState.Error).message)
                }
            }
        }
    }
}