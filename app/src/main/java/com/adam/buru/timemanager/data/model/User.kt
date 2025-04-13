package com.adam.buru.timemanager.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)