package com.example.timetable.screen

import DaysOfMonthScrollBar
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timetable.components.LoadingIndicator
import com.example.timetable.components.StateMessage
import com.example.timetable.components.SubjectCard
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.ui.Alignment
import com.example.timetable.components.ErrorSnackbar
import com.example.timetable.database.TimetableDatabase
import com.example.timetable.model.Schedule
import com.example.timetable.network.ApiClient
import com.example.timetable.utils.fetchData
import com.example.timetable.utils.generateDaysInRange
import com.example.timetable.utils.isLessonNow
import com.example.timetable.utils.parseDateTime
import com.example.timetable.utils.toEntity
import com.example.timetable.utils.toModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(token: String, database: TimetableDatabase) {
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }
    val displayedDays = generateDaysInRange(LocalDate.now(), 30) // Wyświetlenie 30 dni


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
            ScheduleList(token, database, selectedDay)
        }
    }
}


// Lista harmonogramu
@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("NewApi")
@Composable
fun ScheduleList(
    token: String,
    database: TimetableDatabase,
    selectedDay: LocalDate
) {
    var schedules by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    val lastSyncTimestamps = remember { mutableStateMapOf<LocalDate, Long>() } // Mapa ostatnich synchronizacji
    val coroutineScope = rememberCoroutineScope()

    // Funkcja do odświeżania harmonogramu
    fun refreshSchedules() {
        val currentTime = System.currentTimeMillis()
        val lastSyncTime = lastSyncTimestamps[selectedDay]

        // Sprawdzanie, czy synchronizacja była wykonana niedawno
        if (lastSyncTime != null && (currentTime - lastSyncTime) < 30_000) {
            return // Jeśli dane były synchronizowane niedawno, zakończ funkcję
        }

        coroutineScope.launch {
            isRefreshing = true
            try {
                fetchData(
                    apiCall = {
                        ApiClient.service.getSchedulesForDay("Bearer $token", selectedDay.toString())
                    },
                    onSuccess = { apiResponse ->
                        coroutineScope.launch {
                            val entities = apiResponse.map { it.toEntity() }
                            database.timetableDao().insertSchedules(entities) // Zapis do bazy

                            schedules = apiResponse.sortedBy {
                                LocalTime.parse(it.startTime, DateTimeFormatter.ofPattern("HH:mm"))
                            }
                            errorMessage = null
                            showError = false

                            // Aktualizacja znacznika czasu dla wybranego dnia
                            lastSyncTimestamps[selectedDay] = System.currentTimeMillis()
                        }
                    },
                    onError = { error ->
                        errorMessage = error
                        showError = true
                    }
                )
            } finally {
                isRefreshing = false
            }
        }
    }

    // Pobranie danych z bazy przy pierwszym renderowaniu i zmianie dnia
    LaunchedEffect(selectedDay) {
        schedules = database.timetableDao()
            .getSchedulesForDay(selectedDay.toString())
            .map { it.toModel() }
        refreshSchedules() // Odśwież dane z API
    }

    // UI - bez zmian
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { refreshSchedules() }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        when {
            schedules.isEmpty() -> {
                StateMessage(message = "Brak zajęć na wybrany dzień")
            }
            else -> {
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
        }

        // Wyświetlenie dymka błędu
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




