package com.adam.buru.timemanager.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adam.buru.timemanager.data.model.Priority
import com.adam.buru.timemanager.data.repository.TaskRepository
import com.adam.buru.timemanager.util.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.adam.buru.timemanager.data.model.Task

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _taskUIState = MutableStateFlow<TaskUIState>(TaskUIState.Loading)
    val taskUIState: StateFlow<TaskUIState> = _taskUIState

    private val _taskOperationState = MutableStateFlow<TaskOperationState>(TaskOperationState.Idle)
    val taskOperationState: StateFlow<TaskOperationState> = _taskOperationState

    private val _priorities = MutableStateFlow<List<Priority>>(emptyList())
    val priorities: StateFlow<List<Priority>> = _priorities

    fun fetchTasks(context: Context) {
        viewModelScope.launch {
            _taskUIState.value = TaskUIState.Loading
            try {
                val token = AuthManager.getToken(context)
                if (token != null) {
                    val tasks = repository.getAllTasks(token)
                    _taskUIState.value = TaskUIState.Success(tasks)
                } else {
                    _taskUIState.value = TaskUIState.Error("Token not found")
                }
            } catch (e: Exception) {
                _taskUIState.value = TaskUIState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchPriorities(context: Context) {
        viewModelScope.launch {
            _taskUIState.value = TaskUIState.Loading
            try {
                val token = AuthManager.getToken(context)
                if (token != null) {
                    _priorities.value = repository.getAllPriorities(token)
                } else {
                    _taskUIState.value = TaskUIState.Error("Token not found")
                }
            } catch (e: Exception) {
                _taskUIState.value = TaskUIState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getPriorityById(priorityId: Int): String {
        return _priorities.value.find { it.id == priorityId }?.name ?: "Select priority"
    }

    fun getTaskById(id: Int, context: Context) {
        viewModelScope.launch {
            _taskUIState.value = TaskUIState.Loading
            try {
                val token = AuthManager.getToken(context)
                if (token != null) {
                    val task = repository.getTaskById(id, token)
                    _taskUIState.value = TaskUIState.Success(listOf(task))
                } else {
                    _taskUIState.value = TaskUIState.Error("Token not found")
                }
            } catch (e: Exception) {
                _taskUIState.value = TaskUIState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createTask(newTask: Task, context: Context) {
        viewModelScope.launch {
            _taskOperationState.value = TaskOperationState.Loading
            try {
                val token = AuthManager.getToken(context)
                if (token != null) {
                    val message = repository.createTask(newTask, token)
                    _taskOperationState.value = TaskOperationState.Success(message)
                } else {
                    _taskOperationState.value = TaskOperationState.Error("Token not found")
                }
            } catch (e: Exception) {
                _taskOperationState.value = TaskOperationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateTask(updatedTask: Task, context: Context) {
        viewModelScope.launch {
            _taskOperationState.value = TaskOperationState.Loading
            try {
                val token = AuthManager.getToken(context)
                if (token != null) {
                    val message = repository.updateTask(updatedTask.id, updatedTask, token)
                    _taskOperationState.value = TaskOperationState.Success(message)
                } else {
                    _taskOperationState.value = TaskOperationState.Error("Token not found")
                }
            } catch (e: Exception) {
                _taskOperationState.value = TaskOperationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteTask(id: Int, context: Context) {
        viewModelScope.launch {
            _taskOperationState.value = TaskOperationState.Loading
            try {
                val token = AuthManager.getToken(context)
                if (token != null) {
                    val message = repository.deleteTask(id, token)
                    _taskOperationState.value = TaskOperationState.Success(message)
                } else {
                    _taskOperationState.value = TaskOperationState.Error("Token not found")
                }
            } catch (e: Exception) {
                _taskOperationState.value = TaskOperationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteCompletedTasks(context: Context) {
        viewModelScope.launch {
            _taskOperationState.value = TaskOperationState.Loading
            try {
                val token = AuthManager.getToken(context)
                if (token != null) {
                    val tasks = repository.getAllTasks(token)
                    tasks.filter { it.isCompleted }.forEach { task ->
                        repository.deleteTask(task.id, token)
                    }
                    _taskOperationState.value = TaskOperationState.Success("Completed tasks removed successfully")
                } else {
                    _taskOperationState.value = TaskOperationState.Error("Token not found")
                }
            } catch (e: Exception) {
                _taskOperationState.value = TaskOperationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setTaskCompletion(id: Int, isCompleted: Boolean, context: Context) {
        viewModelScope.launch {
            _taskOperationState.value = TaskOperationState.Loading
            try {
                val token = AuthManager.getToken(context)
                if (token != null) {
                    repository.setTaskCompletion(id, isCompleted, token)
                } else {
                    _taskOperationState.value = TaskOperationState.Error("Token not found")
                }
            } catch (e: Exception) {
                _taskOperationState.value = TaskOperationState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class TaskUIState {
    data object Loading : TaskUIState()
    data class Success(val task: List<Task>) : TaskUIState()
    data class Error(val message: String) : TaskUIState()
}

sealed class TaskOperationState {
    data object Idle : TaskOperationState()
    data object Loading : TaskOperationState()
    data class Success(val message: String) : TaskOperationState()
    data class Error(val message: String) : TaskOperationState()
}
