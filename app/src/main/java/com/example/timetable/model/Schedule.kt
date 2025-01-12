package com.example.timetable.model

data class Schedule(
    val id: String,
    val day: String,
    val startTime: String,
    val endTime: String,
    val subject: String,
    val grade: String,
    val classroom: String,
    val teacher: String
)
