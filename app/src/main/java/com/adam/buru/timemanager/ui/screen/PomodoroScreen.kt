package com.adam.buru.timemanager.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adam.buru.timemanager.ui.theme.Green
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen() {
    val studyTime = 25 * 60
    val breakTime = 5 * 60

    var isRunning by rememberSaveable { mutableStateOf(false) }
    var isStudyPhase by rememberSaveable { mutableStateOf(true) }
    var timeLeft by rememberSaveable { mutableIntStateOf( studyTime ) }

    LaunchedEffect(isRunning, isStudyPhase) {
        if (isRunning) {
            while (isRunning && timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            if (timeLeft == 0) {
                isStudyPhase = !isStudyPhase
                timeLeft = if (isStudyPhase) studyTime else breakTime
                isRunning = true
            }
        }
    }

    val backgroundColor = when {
        !isRunning && timeLeft == studyTime -> MaterialTheme.colorScheme.background
        isRunning && isStudyPhase -> MaterialTheme.colorScheme.error
        isRunning && !isStudyPhase -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.background
    }

    val animatedBackgroundColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 1000)
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Study Timer") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(animatedBackgroundColor),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isStudyPhase) "Study Time" else "Break Time",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = String.format(Locale("hu", "HU"), "%02d:%02d", timeLeft / 60, timeLeft % 60),
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                FloatingActionButton(
                    onClick = { isRunning = !isRunning },
                    shape = CircleShape,
                    containerColor = if (isRunning) Color.Yellow else Green
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Play"
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                FloatingActionButton(
                    onClick = {
                        isRunning = false
                        isStudyPhase = true
                        timeLeft = studyTime
                    },
                    shape = CircleShape,
                    containerColor = Color.Red
                ) {
                    Icon(Icons.Filled.Stop, contentDescription = "Reset")
                }
            }
        }
    }
}