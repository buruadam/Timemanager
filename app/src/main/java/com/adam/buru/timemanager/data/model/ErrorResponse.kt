package com.adam.buru.timemanager.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String
)