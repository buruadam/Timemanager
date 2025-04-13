package com.adam.buru.timemanager.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adam.buru.timemanager.data.model.Task
import com.adam.buru.timemanager.navigation.Screen
import com.adam.buru.timemanager.ui.theme.Green
import com.adam.buru.timemanager.ui.theme.Orange
import com.adam.buru.timemanager.ui.viewmodel.TaskOperationState
import com.adam.buru.timemanager.ui.viewmodel.TaskUIState
import com.adam.buru.timemanager.ui.viewmodel.TaskViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    navController: NavController,
    taskViewModel: TaskViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val taskViewState by taskViewModel.taskUIState.collectAsState()
    val taskOperationState by taskViewModel.taskOperationState.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        taskViewModel.fetchTasks(context)
    }

    LaunchedEffect(taskOperationState) {
        when (taskOperationState) {
            is TaskOperationState.Success -> {
                val message = (taskOperationState as TaskOperationState.Success).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                taskViewModel.fetchTasks(context)
            }
            is TaskOperationState.Error -> {
                val message = (taskOperationState as TaskOperationState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Tasks") },
                actions = {
                    IconButton(
                        onClick = { expanded = !expanded }
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Remove completed") },
                            leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = "Remove tasks") },
                            onClick = {
                                taskViewModel.deleteCompletedTasks(context)
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.TaskAddScreen.route) }) {
                Icon(Icons.Filled.Add, contentDescription = "New Task")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->
        when (taskViewState) {
            is TaskUIState.Loading -> {
                Box (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is TaskUIState.Success -> {
                val tasks = (taskViewState as TaskUIState.Success).task
                LazyColumn(modifier = Modifier.padding(contentPadding).padding(16.dp)) {
                    items(tasks) { task ->
                        TaskItem(
                            navController = navController,
                            task = task,
                            taskViewModel = taskViewModel,
                            context = context
                        )
                    }
                }
            }
            is TaskUIState.Error -> {
                val errorMessage = (taskViewState as TaskUIState.Error).message
                Text("Error: $errorMessage", modifier = Modifier.padding(contentPadding).padding(16.dp))
            }
        }
    }
}

@Composable
fun TaskItem(navController: NavController, task: Task, taskViewModel: TaskViewModel, context: Context) {
    var isCompleted by rememberSaveable { mutableStateOf(task.isCompleted) }
    val priorityName = taskViewModel.getPriorityById(task.priorityId)
    val priorityColor = getPriorityColor(task.priorityId)

    ElevatedCard(
        onClick = {
            navController.navigate("${Screen.TaskDetailScreen.route}/${task.id}")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isCompleted) Color.LightGray else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val formattedDate = LocalDateTime.parse(task.dueDate)
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))

            val formattedTime = LocalDateTime.parse(task.dueDate)
                .format(DateTimeFormatter.ofPattern("HH:mm"))

            Text(text = task.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Due Date: $formattedDate, $formattedTime", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = {
                        isCompleted = it
                        taskViewModel.setTaskCompletion(task.id, isCompleted, context)
                    }
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Priority: ",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = priorityName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        imageVector = Icons.Filled.BarChart,
                        contentDescription = "Priority Icon",
                        tint = priorityColor,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

fun getPriorityColor(priorityId: Int): Color {
    return when (priorityId) {
        1 -> Green
        2 -> Orange
        3 -> Color.Red
        else -> Color.Gray
    }
}