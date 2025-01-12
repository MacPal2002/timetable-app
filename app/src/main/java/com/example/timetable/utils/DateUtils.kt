package com.example.timetable.utils

import android.annotation.SuppressLint
import java.time.LocalDate

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

// Funkcja usuwająca wiodące zero z godziny (np. 07:00 -> 7:00)
fun removeLeadingZero(time: String): String {
    return if (time.startsWith("0")) {
        time.substring(1)
    } else {
        time
    }
}