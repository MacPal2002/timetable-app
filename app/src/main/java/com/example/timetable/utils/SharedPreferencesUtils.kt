package com.example.timetable.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.Gson

// Zapisz token użytkownika
fun saveToken(context: Context, token: String) {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    preferences.edit().putString("user_token", token).apply()
}

// Odczytaj token użytkownika
fun getToken(context: Context): String? {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    return preferences.getString("user_token", null) // Zwróci null, jeśli token nie istnieje
}

// Usuń token użytkownika
fun clearToken(context: Context) {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    preferences.edit().remove("user_token").apply()
}

fun saveScreenState(context: Context, screenName: String) {
    val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("last_screen", screenName)
        apply()
    }
}

fun getLastScreenState(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    return sharedPreferences.getString("last_screen", "schedule") // Domyślny ekran to "schedule"
}