package com.adam.buru.timemanager.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adam.buru.timemanager.data.model.User
import com.adam.buru.timemanager.navigation.Screen
import com.adam.buru.timemanager.ui.viewmodel.AuthViewModel
import com.adam.buru.timemanager.ui.viewmodel.RegisterState
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val registerState by authViewModel.registerState.collectAsState()

    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }


    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                val successMessage = (registerState as RegisterState.Success).message
                Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(Screen.RegisterScreen.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
            is RegisterState.Error -> {
                val errorMessage = (registerState as RegisterState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(
            value = firstName, onValueChange = { firstName = it },
            label = { Text("First Name") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = lastName, onValueChange = { lastName = it },
            label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Email") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        FilledTonalButton(
            onClick = {
                val user = User(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password
                )
                authViewModel.register(user)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            enabled = registerState !is RegisterState.Loading
        ) {
            Text("Register")
        }

        OutlinedButton(
            onClick = { navController.navigate(Screen.LoginScreen.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (registerState is RegisterState.Loading) {
            CircularProgressIndicator()
        }
    }
}