package com.example.timetable.screen

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.timetable.components.BottomNavigationBar
import com.example.timetable.components.LogoutButton
import com.example.timetable.database.TimetableDatabase
import com.example.timetable.utils.getLastScreenState
import com.example.timetable.utils.logout
import com.example.timetable.utils.saveScreenState
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    navController: NavHostController,
    token: String,
    onLogout: () -> Unit,
    database: TimetableDatabase,
    context: Context
) {
    val lastScreen = getLastScreenState(context)
    val startDestination = lastScreen ?: "schedule"

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable("schedule") {
                    ScheduleScreen(
                        token = token,
                        database = database
                    )
                    saveScreenState(context, "schedule")
                }
                composable("messages") {
                    MessagesScreen(
                        token = token,
                        database = database
                    )
                    saveScreenState(context, "messages")
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

            // Przycisk wylogowania jako komponent
            val coroutineScope = rememberCoroutineScope()
            LogoutButton(onLogout = {
                coroutineScope.launch {
                    logout(token, onLogout)
                }
            })
        }
    }
}
