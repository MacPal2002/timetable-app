package com.example.timetable.screen

import DaysOfMonthScrollBar
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timetable.components.LoadingIndicator
import com.example.timetable.components.StateMessage
import com.example.timetable.components.SubjectCard
import com.example.timetable.model.Schedule
import com.example.timetable.network.ApiClient
import com.example.timetable.utils.fetchData
import com.example.timetable.utils.generateDaysInRange
import com.example.timetable.utils.isLessonNow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(token: String) {
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }
    val displayedDays = generateDaysInRange(LocalDate.now(), 30) // Wyświetlenie 30 dni
    var schedules by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope() // Zapamiętanie CoroutineScope

    // Pobranie harmonogramu
    LaunchedEffect(selectedDay) {
        isLoading = true
        coroutineScope.launch {
            fetchData(
                apiCall = {
                    ApiClient.service.getSchedulesForDay("Bearer $token", selectedDay.toString())
                },
                onSuccess = { loadedSchedules ->
                    schedules = loadedSchedules.sortedBy {
                        LocalTime.parse(it.startTime, DateTimeFormatter.ofPattern("HH:mm"))
                    }
                    isLoading = false
                },
                onError = { error ->
                    errorMessage = error
                    isLoading = false
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Plan Zajęć", style = MaterialTheme.typography.headlineLarge) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            DaysOfMonthScrollBar(
                daysOfMonth = displayedDays,
                selectedDay = selectedDay,
                onDaySelected = { selectedDay = it }
            )

            when {
                isLoading -> LoadingIndicator()
                errorMessage != null -> StateMessage(message = errorMessage ?: "Błąd podczas ładowania danych")
                schedules.isEmpty() -> StateMessage(message = "Brak zajęć na wybrany dzień")
                else -> ScheduleList(schedules)
            }
        }
    }
}



// Lista harmonogramu
@SuppressLint("NewApi")
@Composable
fun ScheduleList(schedules: List<Schedule>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(schedules) { schedule ->
            val isCurrentLesson = isLessonNow(schedule, LocalTime.now(), LocalDate.now())
            SubjectCard(schedule, isCurrentLesson)
        }
    }
}
