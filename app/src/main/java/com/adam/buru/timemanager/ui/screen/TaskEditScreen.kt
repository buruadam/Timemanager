package com.adam.buru.timemanager.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adam.buru.timemanager.data.model.Task
import com.adam.buru.timemanager.navigation.Screen
import com.adam.buru.timemanager.ui.screen.modal.DatePickerModal
import com.adam.buru.timemanager.ui.screen.modal.TimePickerModal
import com.adam.buru.timemanager.ui.viewmodel.TaskOperationState
import com.adam.buru.timemanager.ui.viewmodel.TaskViewModel
import com.adam.buru.timemanager.ui.viewmodel.TaskUIState
import com.adam.buru.timemanager.util.AuthManager
import com.adam.buru.timemanager.util.DateHelper.convertMillisToDateString
import com.adam.buru.timemanager.util.DateHelper.convertTimeToString
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    navController: NavController,
    taskId: Int,
    taskViewModel: TaskViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val taskUIState by taskViewModel.taskUIState.collectAsState()
    val taskOperationState by taskViewModel.taskOperationState.collectAsState()
    val priorities by taskViewModel.priorities.collectAsState()

    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val priorityId = remember { mutableIntStateOf(0) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime: TimePickerState? by remember { mutableStateOf(null) }

    var showDateModal by remember { mutableStateOf(false) }
    var showTimeModal by remember { mutableStateOf(false)}
    var expandedPriority by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        taskViewModel.getTaskById(taskId, context)
    }

    LaunchedEffect(context) {
        taskViewModel.fetchPriorities(context)
    }

    LaunchedEffect(taskUIState) {
        when (taskUIState) {
            is TaskUIState.Success -> {
                (taskUIState as TaskUIState.Success).task.firstOrNull()?.let { task ->
                    title.value = task.title
                    description.value = task.description
                    priorityId.intValue = task.priorityId

                    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val localDateTime = LocalDateTime.parse(task.dueDate, dateTimeFormatter)
                    val datePart = localDateTime.toLocalDate().toString()
                    val parsedDate = dateFormatter.parse(datePart)
                    val timePart = localDateTime.toLocalTime()

                    selectedDate = parsedDate?.time

                    selectedTime = TimePickerState(
                        initialHour = timePart.hour,
                        initialMinute = timePart.minute,
                        is24Hour = true
                    )
                }
            }
            is TaskUIState.Error -> {
                val errorMessage = (taskUIState as TaskUIState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
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
                title = { Text("Edit Task") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                TextButton(
                    onClick = { showDateModal = true },
                    modifier = Modifier.weight(1f),
                    shape = RectangleShape
                ) {
                    Text(
                        text = selectedDate?.let { convertMillisToDateString(it) } ?: "Select date",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { showTimeModal = true },
                    modifier = Modifier.weight(1f),
                    shape = RectangleShape
                ) {
                    Text(
                        text = selectedTime?.let { convertTimeToString(it) } ?: "Select time",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expandedPriority,
                onExpandedChange = { expandedPriority = it }
            ) {
                OutlinedTextField(
                    value = taskViewModel.getPriorityById(priorityId.intValue),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = expandedPriority,
                    onDismissRequest = { expandedPriority = false }
                ) {
                    priorities.forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority.name) },
                            onClick = {
                                priorityId.intValue = priority.id
                                expandedPriority = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilledTonalButton(
                onClick = {
                    coroutineScope.launch {
                        val dateValue = selectedDate?.let { convertMillisToDateString(it) } ?: ""
                        val timeValue = convertTimeToString(selectedTime)

                        val updatedTask = Task(
                            id = taskId,
                            title = title.value,
                            description = description.value,
                            dueDate = "${dateValue}T${timeValue}",
                            priorityId = priorityId.intValue,
                            userId = AuthManager.getUserId(context)
                        )
                        taskViewModel.updateTask(updatedTask, context)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = taskOperationState !is TaskOperationState.Loading
            ) {
                Text("Update")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (taskOperationState is TaskOperationState.Loading) {
                CircularProgressIndicator()
            }
        }

        if (showDateModal) {
            DatePickerModal(
                initialDateMillis = selectedDate,
                onDateSelected = { selectedDate = it },
                onDismiss = { showDateModal = false }
            )
        }

        if (showTimeModal) {
            TimePickerModal(
                initialTime = selectedTime ?: rememberTimePickerState(
                    0,
                    0,
                    is24Hour = true
                ),
                onConfirm = { newTimeState ->
                    selectedTime = newTimeState
                    showTimeModal = false
                },
                onDismiss = { showTimeModal = false }
            )
        }
    }
}