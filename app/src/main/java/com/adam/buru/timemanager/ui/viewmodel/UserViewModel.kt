package com.adam.buru.timemanager.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adam.buru.timemanager.data.model.User
import com.adam.buru.timemanager.data.repository.UserRepository
import com.adam.buru.timemanager.util.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _userUIState = MutableStateFlow<UserUIState>(UserUIState.Idle)
    val userUIState: StateFlow<UserUIState> = _userUIState

    fun getUserById(userId: Int, context: Context) {
        viewModelScope.launch {
            _userUIState.value = UserUIState.Loading
            try {
                val token = AuthManager.getToken(context)
                if (token != null) {
                    val user = repository.getUserById(userId, token)
                    _userUIState.value = UserUIState.Success(user)
                }
                else {
                    _userUIState.value = UserUIState.Error("Token not found")
                }

            } catch (e: Exception) {
                _userUIState.value = UserUIState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class UserUIState {
    data object Idle : UserUIState()
    data object Loading : UserUIState()
    data class Success(val user: User) : UserUIState()
    data class Error(val message: String) : UserUIState()
}