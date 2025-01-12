package com.example.timetable.model

data class Message(
    val id: String,
    val from: String,
    val to: String,
    val subject: String,
    val body: String,
    val date: String,
    val time: String,
    val readed: Boolean
)
