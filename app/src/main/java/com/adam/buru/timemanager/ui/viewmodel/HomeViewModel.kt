package com.adam.buru.timemanager.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adam.buru.timemanager.data.repository.TaskRepository
import com.adam.buru.timemanager.util.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _completedTasks = MutableStateFlow(0)
    val completedTasks: StateFlow<Int> = _completedTasks

    private val _totalTasks = MutableStateFlow(0)
    val totalTasks: StateFlow<Int> = _totalTasks

    fun fetchTaskCounts(context: Context) {
        viewModelScope.launch {
            val token = AuthManager.getToken(context)
            if (token != null) {
                _totalTasks.value = repository.getTaskCountByUserId(token)
                _completedTasks.value = repository.getDoneTaskCountByUserId(token)
            } else {
                _totalTasks.value = 0
                _completedTasks.value = 0
            }
        }
    }
}
