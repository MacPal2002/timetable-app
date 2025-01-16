package com.example.timetable.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.timetable.components.LoadingIndicator
import com.example.timetable.components.MessageItem
import com.example.timetable.components.StateMessage
import com.example.timetable.model.Message
import com.example.timetable.network.ApiClient
import com.example.timetable.utils.fetchData
import com.example.timetable.utils.parseDateTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(token: String) {
    var selectedMessageId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Wiadomości", style = MaterialTheme.typography.headlineLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (selectedMessageId == null) {
                MessagesList(token) { messageId ->
                    selectedMessageId = messageId
                }
            } else {
                MessageDetailsScreen(token, selectedMessageId!!) {
                    selectedMessageId = null
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessagesList(token: String, onMessageSelected: (String) -> Unit) {
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        fetchData(
            apiCall = { ApiClient.service.getMessages("Bearer $token") },
            onSuccess = { apiResponse ->
                messages = apiResponse.sortedByDescending { message ->
                    parseDateTime(message.date, message.time)
                }
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }

    when {
        isLoading -> LoadingIndicator()
        errorMessage != null -> StateMessage(errorMessage ?: "Błąd podczas ładowania danych")
        messages.isEmpty() -> StateMessage(message = "Brak wiadomości")
        else -> {
            LazyColumn {
                items(messages) { message ->
                    MessageItem(message, onClick = { onMessageSelected(message.id) })
                }
            }
        }
    }
}


