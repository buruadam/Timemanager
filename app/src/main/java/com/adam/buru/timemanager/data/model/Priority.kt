package com.adam.buru.timemanager.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Priority(
    val id: Int,
    val name: String
)