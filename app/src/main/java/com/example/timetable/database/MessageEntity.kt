package com.example.timetable.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val subject: String,
    val body: String,
    val from: String,
    val date: String,
    val time: String,
    val to: String,
    val read: Boolean
)
