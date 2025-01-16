package com.example.timetable.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey val id: String,
    val day: String,
    val startTime: String,
    val endTime: String,
    val subject: String,
    val classroom: String,
    val teacher: String,
    val course: String,
    val group: String,
)
