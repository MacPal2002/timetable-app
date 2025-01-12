package com.example.timetable.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.timetable.network.ApiClient
import com.example.timetable.utils.getLastScreenState
import com.example.timetable.utils.saveScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException

@Composable
fun MainScreen(navController: NavHostController, token: String, onLogout: () -> Unit) {
    val context = LocalContext.current
    val lastScreen = getLastScreenState(context)

    // Funkcja logout
    val logout: suspend () -> Unit = {
        try {
            // Wywołanie API do wylogowania
            val response = ApiClient.service.logoutUser("Bearer $token")

            if (response.isSuccessful) {
                // Sprawdzamy, czy odpowiedź była sukcesem (kod 200 OK)
                val apiResponse = response.body()
                if (apiResponse?.error == null) {
                    onLogout()
                    Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Logout failed: ${apiResponse.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Jeśli odpowiedź nie była sukcesem
                Toast.makeText(context, "Logout failed with status: ${response.code()}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            // Obsługuje wyjątki związane z problemami z siecią
            val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().remove("token").apply()

            onLogout()
            Toast.makeText(context, "Logged out due to network issue", Toast.LENGTH_SHORT).show()
        } catch (e: HttpException) {
            Toast.makeText(context, "Logout failed with status: ${e.code()}", Toast.LENGTH_SHORT).show()
        } catch (e: TimeoutException) {
            Toast.makeText(context, "Logout failed: Timeout", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Logout failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    Scaffold(
        bottomBar = {
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
                            navController.navigate("schedule") {
                                saveScreenState(context, "schedule")
                            }
                        }
                    ) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Plan Zajęć")
                        Text(text = "Plan Zajęć", color = Color.White)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            navController.navigate("messages") {
                                saveScreenState(context, "messages")
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Email, contentDescription = "Wiadomości")
                        Text(text = "Wiadomości", color = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = lastScreen ?: "schedule"
            ) {
                composable("schedule") {
                    ScheduleScreen(token = token)
                }
                composable("messages") {
                    MessagesScreen(
                        token = token,
                        onMessageSelected = { messageId ->
                            navController.navigate("message_details/$messageId")
                        }
                    )
                }
                composable("message_details/{messageId}") { backStackEntry ->
                    val messageId = backStackEntry.arguments?.getString("messageId") ?: ""
                    MessageDetailsScreen(token = token, messageId = messageId)
                }
            }

            // Przycisk wylogowania
            Button(
                onClick = {
                    // Wywołanie funkcji logout
                    CoroutineScope(Dispatchers.Main).launch {
                        logout()
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(text = "Logout")
            }
        }
    }
}



