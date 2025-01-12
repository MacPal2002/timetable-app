package com.example.timetable.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timetable.model.Message
import com.example.timetable.network.ApiClient
import java.io.IOException

@Composable
fun MessagesScreen(token: String, onMessageSelected: (String) -> Unit) {
    // Stan przechowujący listę wiadomości
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

// Korutyna do załadowania wiadomości z API
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val response = ApiClient.service.getMessages("Bearer $token")

            if (response.isSuccessful) {
                // Sprawdzamy, czy odpowiedź była sukcesem (kod 200 OK)
                val apiResponse = response.body()
                if (apiResponse?.error == null) {
                    // Jeśli brak błędu w odpowiedzi, zapisujemy dane
                    if (apiResponse != null) {
                        messages = apiResponse.data ?: emptyList()
                    }
                } else {
                    // Jeśli odpowiedź zawiera błąd, wyświetlamy komunikat o błędzie
                    errorMessage = "Błąd: ${apiResponse.message}"
                }
            } else {
                // Obsługuje odpowiedź z błędem (np. kod 400, 500)
                errorMessage = "Nie udało się załadować wiadomości. Status: ${response.code()}"
            }
        } catch (e: IOException) {
            // Obsługuje wyjątki związane z problemami z siecią
            errorMessage = "Błąd połączenia: ${e.message}"
        } catch (e: Exception) {
            // Obsługujemy inne wyjątki
            errorMessage = "Nie udało się załadować wiadomości: ${e.message}"
        } finally {
            isLoading = false
        }
    }


    // Interfejs użytkownika
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Wiadomości", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    )
                }
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Wystąpił błąd",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
            messages.isEmpty() -> {
                Text(
                    text = "Brak wiadomości",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            else -> {
                LazyColumn {
                    items(messages) { message ->
                        MessageItem(message, onClick = { onMessageSelected(message.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: Message, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .clickable { onClick() }
    ) {
        Text(text = message.subject, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "From: ${message.from}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message.body, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Date: ${message.date} | Time: ${message.time}", style = MaterialTheme.typography.bodyMedium)
        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun MessageDetailsScreen(token: String, messageId: String) {
    // Stan przechowujący jedną wiadomość
    var message by remember { mutableStateOf<Message?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(messageId) {
        try {
            isLoading = true
            val response = ApiClient.service.getMessage("Bearer $token", messageId)

            if (response.isSuccessful) {
                // Sprawdzamy, czy odpowiedź była sukcesem (kod 200 OK)
                val apiResponse = response.body()
                if (apiResponse?.error == null) {
                    // Jeśli brak błędu w odpowiedzi, zapisujemy dane
                    if (apiResponse != null) {
                        message = apiResponse.data
                    }
                } else {
                    // Jeśli odpowiedź zawiera błąd, wyświetlamy komunikat o błędzie
                    errorMessage = "Błąd: ${apiResponse.message}"
                }
            } else {
                // Obsługuje odpowiedź z błędem (np. kod 400, 500)
                errorMessage = "Nie udało się załadować wiadomości. Status: ${response.code()}"
            }
        } catch (e: IOException) {
            // Obsługuje wyjątki związane z problemami z siecią
            errorMessage = "Błąd połączenia: ${e.message}"
        } catch (e: Exception) {
            // Obsługujemy inne wyjątki
            errorMessage = "Nie udało się załadować wiadomości: ${e.message} $messageId"
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Padding dla całego ekranu
            .background(MaterialTheme.colorScheme.background) // Tło dla całego ekranu
            .wrapContentSize(Alignment.Center) // Wyrównanie do środka
    ) {
        when {
            isLoading -> {
                // Stylizacja ładowania
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center) // Wyrównanie na środku ekranu
                        .padding(top = 16.dp)
                )
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Wystąpił błąd",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center) // Wyrównanie na środku ekranu
                        .padding(top = 16.dp)
                )
            }
            message != null -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f) // Wysokość karty ustawiona na 80% ekranu
                        .padding(8.dp),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Tytuł wiadomości
                        Text(
                            text = message!!.subject,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp)) // Linia pozioma

                        // Autor wiadomości
                        Text(
                            text = "From: ${message!!.from}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp)) // Linia pozioma

                        // Treść wiadomości
                        Text(
                            text = message!!.body,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }
    }
}











