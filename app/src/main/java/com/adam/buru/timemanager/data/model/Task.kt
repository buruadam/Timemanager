package com.adam.buru.timemanager.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int = 0,
    val userId: Int?,
    val priorityId: Int,
    val isCompleted: Boolean = false,
    val title: String,
    val description: String,
    val dueDate: String
)