package com.example.timetable.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.timetable.MainActivity
import com.example.timetable.components.ErrorSnackbar
import com.example.timetable.components.MessageItem
import com.example.timetable.components.StateMessage
import com.example.timetable.database.TimetableDatabase
import com.example.timetable.model.Message
import com.example.timetable.network.ApiClient
import com.example.timetable.utils.fetchData
import com.example.timetable.utils.parseDateTime
import com.example.timetable.utils.toEntity
import com.example.timetable.utils.toModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(token: String, database: TimetableDatabase) {
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
                MessagesList(token, database) { messageId ->
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

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessagesList(
    token: String,
    database: TimetableDatabase,
    onMessageSelected: (String) -> Unit
) {
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    val lastSyncTimestamp = remember { mutableLongStateOf(0L) } // Przechowuje czas ostatniej synchronizacji
    val coroutineScope = rememberCoroutineScope()

    // Uzyskaj Context z @Composable i przekaż do fetchData
    val context = LocalContext.current

    // Funkcja do odświeżania wiadomości
    fun refreshMessages(force: Boolean = false) {
        val currentTime = System.currentTimeMillis()
        val lastSyncTime = lastSyncTimestamp.longValue

        // Zapobiegaj zbyt częstemu odświeżaniu (jeśli synchronizacja była niedawno)
        if (!force && (currentTime - lastSyncTime) < 30_000) {
            return
        }

        coroutineScope.launch {
            isRefreshing = true
            try {
                fetchData(
                    apiCall = { ApiClient.service.getMessages("Bearer $token") },
                    onSuccess = { apiResponse ->
                        coroutineScope.launch {
                            val entities = apiResponse.map { it.toEntity() }

                            // Usuń wiadomości, które już nie istnieją w danych API
                            val apiMessageIds = entities.map { it.id }
                            database.timetableDao().deleteMessagesNotIn(apiMessageIds)

                            // Zapisz nowe wiadomości z API w bazie danych
                            database.timetableDao().insertMessages(entities)

                            // Ustaw wiadomości do wyświetlenia w UI
                            messages = apiResponse.sortedByDescending { message ->
                                parseDateTime(message.date, message.time)
                            }

                            errorMessage = null
                            showError = false
                            lastSyncTimestamp.longValue = System.currentTimeMillis()
                        }
                    },
                    onError = { error ->
                        errorMessage = error
                        showError = true
                    },
                    onUnauthorized = {
                        (context as? MainActivity)?.handleUnauthorized()
                    }
                )
            } finally {
                isRefreshing = false
            }
        }
    }


    // Pierwsze pobranie wiadomości przy renderowaniu
    LaunchedEffect(Unit) {
        messages = database.timetableDao()
            .getMessages()
            .map { it.toModel() }
        refreshMessages()
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { refreshMessages(force = true) }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        when {
            messages.isEmpty() && errorMessage != null -> {
                StateMessage(message = "Brak danych")
            }
            messages.isEmpty() -> {
                StateMessage(message = "Brak wiadomości")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(messages) { message ->
                        MessageItem(message, onClick = { onMessageSelected(message.id) })
                    }
                }
            }
        }

        // Wyświetlenie dymka błędu, jeśli jest aktywne showError
        ErrorSnackbar(
            errorMessage = errorMessage,
            isVisible = showError,
            onDismiss = { showError = false }
        )

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.background
        )
    }
}




