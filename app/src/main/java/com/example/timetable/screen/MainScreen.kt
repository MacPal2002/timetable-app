package com.example.timetable.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.timetable.components.BottomNavigationBar
import com.example.timetable.utils.logout
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(navController: NavHostController, token: String, onLogout: () -> Unit) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = "schedule"
            ) {
                composable("schedule") {
                    ScheduleScreen(token = token)
                }
                composable("messages") {
                    MessagesScreen(token = token)
                }
                composable("message_details/{messageId}") { backStackEntry ->
                    val messageId = backStackEntry.arguments?.getString("messageId") ?: ""
                    MessageDetailsScreen(
                        token = token,
                        messageId = messageId,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // Przycisk wylogowania
            val coroutineScope = rememberCoroutineScope()

            Button(
                onClick = {
                    coroutineScope.launch {
                        logout(token, onLogout) // Wywołanie funkcji logout w korutynie
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
