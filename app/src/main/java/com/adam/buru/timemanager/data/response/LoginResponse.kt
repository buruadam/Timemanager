package com.adam.buru.timemanager.data.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String? = null,
    val error: String? = null
)