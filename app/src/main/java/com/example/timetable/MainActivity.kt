package com.example.timetable

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.timetable.database.TimetableDatabase
import com.example.timetable.ui.theme.TimetableTheme
import com.example.timetable.screen.MainScreen
import com.example.timetable.screen.LoginScreen
import com.example.timetable.utils.clearToken
import com.example.timetable.utils.getToken
import com.example.timetable.utils.saveToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val _unauthorizedFlow = MutableStateFlow(false) // Stan błędu 401
    val unauthorizedFlow: StateFlow<Boolean> get() = _unauthorizedFlow

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimetableTheme(dynamicColor = false) {
                App()
            }
        }
    }

    fun handleUnauthorized() {
        lifecycleScope.launch {
            clearToken(applicationContext)
            _unauthorizedFlow.value = true // Ustawienie błędu 401
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App() {
    val context = LocalContext.current
    val navController = rememberNavController()

    // Inicjalizacja bazy danych Room
    val database = remember {
        Room.databaseBuilder(
            context,
            TimetableDatabase::class.java,
            "timetable_database"
        ).build()
    }

    // Pobieranie instancji MainActivity i stanu autoryzacji
    val mainActivity = LocalContext.current as MainActivity
    val isUnauthorized by mainActivity.unauthorizedFlow.collectAsState(initial = false)
    var token by remember { mutableStateOf(getToken(context)) }

    // Wyświetlanie odpowiedniego ekranu w zależności od stanu autoryzacji
    if (isUnauthorized || token == null) {
        LoginScreen(onTokenReceived = { receivedToken ->
            token = receivedToken
            mainActivity.lifecycleScope.launch {
                saveToken(context, receivedToken)
            }
        })
    } else {
        MainScreen(
            navController = navController,
            token = token!!,
            onLogout = {
                mainActivity.lifecycleScope.launch {
                    clearToken(context)
                    token = null
                }
            },
            database = database,
            context = context
        )
    }
}