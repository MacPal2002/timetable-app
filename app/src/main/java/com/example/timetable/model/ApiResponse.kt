package com.example.timetable.model

data class ApiResponse<T>(
    val error: String?,
    val message: String?,
    val data: T?,
    val token: String? = null
)
