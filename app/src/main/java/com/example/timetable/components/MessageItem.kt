package com.example.timetable.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timetable.model.Message

@Composable
fun MessageItem(message: Message, onClick: () -> Unit) {
    // Funkcja do przycinania tekstu
    fun truncateText(text: String, maxLength: Int): String {
        return if (text.length > maxLength) {
            text.take(maxLength) + "..."
        } else {
            text
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .clickable { onClick() }
    ) {
        // Tytuł wiadomości
        Text(
            text = message.subject,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Treść wiadomości (przycięta)
        Text(
            text = truncateText(message.body, maxLength = 100), // Limit 100 znaków
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Data i czas wiadomości
        Text(
            text = "Date: ${message.date} | Time: ${message.time}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Separator
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}