package com.example.timetable.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.timetable.model.ApiResponse
import com.example.timetable.model.User
import com.example.timetable.network.ApiClient
import com.example.timetable.utils.parseErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@Composable
fun LoginScreen(onTokenReceived: (String) -> Unit) {
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) } // Dodanie zmiennej isLoading

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Funkcja logowania
    fun loginUser() {
        coroutineScope.launch {
            isLoading = true // Ustawienie isLoading na true, zaczynamy ładowanie
            val result = handleLogin(username.text, password.text)
            result.onSuccess {
                it.token?.let { token -> onTokenReceived(token) }
            }
            result.onFailure {
                errorMessage = it.message ?: "Nie udało się zalogować"
            }
            isLoading = false // Po zakończeniu operacji ustawiamy isLoading na false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Wyświetlanie przycisku lub wskaźnika ładowania
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        } else {
            Button(onClick = { loginUser() }) {
                Text(text = "Login")
            }
        }
    }
}

suspend fun handleLogin(username: String, password: String): Result<ApiResponse<Any>> {
    return try {
        val user = User(username = username, password = password)

        // Wykonanie zapytania
        val response = ApiClient.service.loginUser(user)

        if (response.isSuccessful) {
            // Odpowiedź sukcesu
            response.body()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Brak danych w odpowiedzi"))
        } else {
            // Obsługa błędnej odpowiedzi
            val errorResponse = response.errorBody()?.string()
            val apiError = errorResponse?.let {
                try {
                    Gson().fromJson(it, ApiResponse::class.java)
                } catch (e: Exception) {
                    null
                }
            }

            // Komunikat błędu z `ApiResponse` lub domyślny
            val errorMessage = apiError?.message ?: "Błąd logowania: ${response.code()} ${response.message()}"
            Result.failure(Exception(errorMessage))
        }
    } catch (e: IOException) {
        // Obsługa błędów sieciowych
        Result.failure(Exception("Brak połączenia z internetem"))
    } catch (e: Exception) {
        // Inne błędy
        Result.failure(Exception("Wystąpił błąd: ${e.message}"))
    }
}










