package com.example.timetable

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.timetable.database.TimetableDatabase
import com.example.timetable.ui.theme.TimetableTheme
import com.example.timetable.screen.MainScreen
import com.example.timetable.screen.LoginScreen
import com.example.timetable.utils.clearToken
import com.example.timetable.utils.getToken
import com.example.timetable.utils.saveToken

class MainActivity : ComponentActivity() {
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
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App() {
    val context = LocalContext.current
    val navController = rememberNavController()  // Inicjalizacja NavController
    // Tworzenie instancji Room Database
    val database = Room.databaseBuilder(
        context, // Użycie kontekstu aplikacji
        TimetableDatabase::class.java,
        "timetable_database"
    ).build()

    // Odczytanie tokenu z SharedPreferences
    var token by remember { mutableStateOf(getToken(context)) }

    if (token == null) {
        // Jeśli token nie istnieje, wyświetl ekran logowania
        LoginScreen(onTokenReceived = { receivedToken ->
            token = receivedToken
            saveToken(context, receivedToken) // Zapisz token po zalogowaniu
        })
    } else {
        // Jeśli token istnieje, przejdź do głównego ekranu
        MainScreen(
            navController = navController, token = token!!, onLogout = {
                // Usuń token przy wylogowaniu
                clearToken(context)
                token = null
            },
            database = database,
            context = context
        )
    }
}

