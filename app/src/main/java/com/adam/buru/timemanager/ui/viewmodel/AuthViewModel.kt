package com.adam.buru.timemanager.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adam.buru.timemanager.data.model.User
import com.adam.buru.timemanager.data.repository.AuthRepository
import com.adam.buru.timemanager.util.AuthManager
import com.auth0.jwt.JWT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun login(email: String, password: String, context: Context) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = authRepository.login(email, password)
                val token = response.token

                if (token != null) {
                    val userId = extractUserIdFromToken(token)
                    _loginState.value = LoginState.Success(token)

                    AuthManager.saveToken(token, userId, context)
                } else {
                    _loginState.value = LoginState.Error(response.error ?: "Unknown error")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun register(user: User) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val message = authRepository.register(user)
                _registerState.value = RegisterState.Success(message)
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun extractUserIdFromToken(token: String): Int {
        return try {
            val jwt = JWT.decode(token)
            jwt.getClaim("id").asInt()
        } catch (e: Exception) {
            -1
        }
    }
}

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegisterState {
    data object Idle : RegisterState()
    data object Loading : RegisterState()
    data class Success(val message: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}