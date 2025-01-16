package com.example.timetable.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.timetable.model.Schedule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
fun generateDaysInRange(startDate: LocalDate, monthsToAdd: Long): List<LocalDate> {
    // Obliczanie daty końcowej
    val endDate = startDate.plusMonths(monthsToAdd)

    val daysList = mutableListOf<LocalDate>()
    var currentDay = startDate

    // Generowanie dni od startDate do endDate
    while (!currentDay.isAfter(endDate)) {
        daysList.add(currentDay)
        currentDay = currentDay.plusDays(1)
    }

    return daysList
}

@RequiresApi(Build.VERSION_CODES.O)
fun isLessonNow(schedule: Schedule, currentTime: LocalTime, currentDate: LocalDate): Boolean {
    try {
        // Konwertowanie godzin rozpoczęcia i zakończenia lekcji na obiekt LocalTime
        val startTime = LocalTime.parse(schedule.startTime, DateTimeFormatter.ofPattern("HH:mm"))  // Użyj formatu 24-godzinnego
        val endTime = LocalTime.parse(schedule.endTime, DateTimeFormatter.ofPattern("HH:mm"))  // Użyj formatu 24-godzinnego

        // Zaokrąglanie aktualnego czasu do minut
        val currentTimeRounded = currentTime.withSecond(0).withNano(0)

        // Sprawdzanie, czy dzisiaj jest dzień lekcji i czy aktualny czas mieści się w przedziale czasowym lekcji
        return currentDate.toString() == schedule.day &&
                currentTimeRounded.isAfter(startTime) &&
                currentTimeRounded.isBefore(endTime)
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

@SuppressLint("NewApi")
fun parseDateTime(date: String, time: String): Long {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.parse("$date $time", formatter)
        dateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
    } catch (e: Exception) {
        0L // Domyślna wartość w przypadku błędu
    }
}