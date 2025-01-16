package com.example.timetable.model

data class ApiResponse<T>(
    val error: Boolean,
    val message: String?,
    val data: T?,
    val token: String? = null
)
