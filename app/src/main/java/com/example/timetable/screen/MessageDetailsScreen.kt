package com.example.timetable.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.timetable.components.LoadingIndicator
import com.example.timetable.components.StateMessage
import com.example.timetable.model.Message
import com.example.timetable.network.ApiClient
import com.example.timetable.utils.fetchData

@Composable
fun MessageDetailsScreen(token: String, messageId: String, onBack: () -> Unit) {
    var message by remember { mutableStateOf<Message?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(messageId) {
        fetchData(
            apiCall = { ApiClient.service.getMessage("Bearer $token", messageId) },
            onSuccess = { apiResponse ->
                message = apiResponse
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Button(
            onClick = onBack,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Powrót")
        }

        when {
            isLoading -> LoadingIndicator()
            errorMessage != null -> StateMessage(errorMessage ?: "Błąd podczas ładowania danych")
            message != null -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            item {
                                Text(
                                    text = message!!.subject,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            item {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    thickness = 1.dp, // Grubość dividera
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                )
                            }
                            item {
                                Text(
                                    text = "From: ${message!!.from}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            item {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    thickness = 1.dp, // Grubość dividera
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                )
                            }
                            item {
                                Text(
                                    text = message!!.body,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Justify
                                )
                            }
                        }
                    }

                    Text(
                        text = "Date: ${message!!.date} | Time: ${message!!.time}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.End)
                    )
                }
            }
        }
    }
}