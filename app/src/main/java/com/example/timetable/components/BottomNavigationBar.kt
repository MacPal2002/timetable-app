package com.example.timetable.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    navController.navigate("schedule")
                }
            ) {
                Icon(Icons.Filled.DateRange, contentDescription = "Plan Zajęć")
                Text(text = "Plan Zajęć", color = Color.White)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    navController.navigate("messages")
                }
            ) {
                Icon(Icons.Filled.Email, contentDescription = "Wiadomości")
                Text(text = "Wiadomości", color = Color.White)
            }
        }
    }
}
