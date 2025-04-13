package com.adam.buru.timemanager.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
object DateHelper {
    fun convertMillisToDateString(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun convertTimeToString(time: TimePickerState?): String {
        return if (time != null) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, time.hour)
                set(Calendar.MINUTE, time.minute)
            }
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            timeFormat.format(calendar.time)
        } else {
            "00:00"
        }
    }
}