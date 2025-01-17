package com.example.timetable.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun LogoutButton(onLogout: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth() // Użycie pełnej szerokości kontenera
    ) {
        Button(
            onClick = { onLogout() },
            modifier = Modifier
                .align(Alignment.TopEnd) // Wyrównanie do prawego górnego rogu
                .padding(top = 16.dp, end = 16.dp) // Odstęp od górnej i prawej krawędzi
        ) {
            Text(text = "Logout")
        }
    }
}

