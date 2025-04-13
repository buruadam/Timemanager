package com.adam.buru.timemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.adam.buru.timemanager.navigation.AppNavigation
import com.adam.buru.timemanager.ui.theme.TimemanagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TimemanagerTheme(darkTheme = false) {
                AppNavigation()
            }
        }
    }
}