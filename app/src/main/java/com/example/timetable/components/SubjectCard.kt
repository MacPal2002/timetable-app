package com.example.timetable.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.timetable.model.Schedule

@Composable
fun SubjectCard(schedule: Schedule, isLessonNow: Boolean) {
    val backgroundColor = if (isLessonNow) {
        if (isSystemInDarkTheme()) Color(0xFF009688) else Color(0xFFFFF3E0) // Jasny blady pomarańczowy
    } else {
        if (isSystemInDarkTheme()) Color(0xFF232027) else Color(0xFFFFFBF2) // Kremowy w jasnym trybie
    }

    val textColor = if (isLessonNow) {
        if (isSystemInDarkTheme()) Color.White else Color(0xFF1C1B1F) // Ciemny tekst w jasnym trybie
    } else {
        if (isSystemInDarkTheme()) Color.White else Color(0xFF1C1B1F) // Ciemny tekst dla nieaktywnych zajęć
    }

    val cardElevation = if (isLessonNow) 8.dp else 2.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(cardElevation),
        colors = CardDefaults.cardColors(backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = schedule.subject, style = MaterialTheme.typography.bodyLarge, color = textColor, fontWeight = FontWeight.ExtraBold )
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
            Text(text = "Time: ${schedule.startTime} - ${schedule.endTime}", color = textColor)
            Text(text = "Classroom: ${schedule.classroom}", color = textColor)
            Text(text = "Teacher: ${schedule.teacher}", color = textColor)
        }
    }
}