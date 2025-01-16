package com.example.timetable.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
@Composable
fun DayItem(day: LocalDate, selectedDay: LocalDate, onClick: () -> Unit) {
    val isSelected = day == selectedDay
    Text(
        text = day.format(DateTimeFormatter.ofPattern("EEE dd.MM")),  // Formatowanie daty
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp)) // Zaokrąglenie narożników
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            )
            .clickable { onClick() }  // Akcja po kliknięciu
            .padding(12.dp),  // Padding wewnętrzny
        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface  // Kolor tekstu w zależności od wybrania
    )
}
