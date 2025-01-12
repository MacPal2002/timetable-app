package com.example.timetable.screen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.timetable.model.Schedule
import com.example.timetable.network.ApiClient
import com.example.timetable.screen.components.DayItem
import com.example.timetable.utils.generateDaysInRange
import com.example.timetable.utils.removeLeadingZero
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
@SuppressLint("NewApi")
fun ScheduleScreen(token: String) {
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }
    val displayedDays = generateDaysInRange(LocalDate.now(), 1) // Pobranie dni na 1 miesiąc do przodu
    var schedules by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var dataLoadedSuccessfully by remember { mutableStateOf(true) } // Flaga do sprawdzenia, czy dane zostały załadowane poprawnie

    // Korutyna do załadowania planu zajęć z API dla wybranego dnia
    LaunchedEffect(selectedDay) {
        try {
            isLoading = true
            dataLoadedSuccessfully = true // Zakładamy, że dane się załadują poprawnie

            // Wywołanie metody API, aby pobrać harmonogram tylko dla wybranego dnia
            val response = ApiClient.service.getSchedulesForDay("Bearer $token", selectedDay.toString())

            if (response.isSuccessful) {
                // Jeśli odpowiedź jest udana (status 200-299), przetwarzamy dane
                val apiResponse = response.body()
                if (apiResponse?.error == null) {
                    if (apiResponse != null) {
                        schedules = apiResponse.data ?: emptyList()
                    } // Mapowanie danych z odpowiedzi API
                } else {
                    // Jeśli odpowiedź zawiera błąd, wyświetlamy komunikat o błędzie
                    errorMessage = "Błąd: ${apiResponse.message}"
                    dataLoadedSuccessfully = false
                }
            } else {
                // Obsługuje odpowiedź z błędem (np. kod 400, 500)
                errorMessage = "Nie udało się załadować planu zajęć. Status: ${response.code()}"
                dataLoadedSuccessfully = false
            }
        } catch (e: IOException) {
            // Obsługuje wyjątki związane z problemami z siecią
            errorMessage = "Błąd połączenia: ${e.message}"
            dataLoadedSuccessfully = false
        } catch (e: Exception) {
            // Obsługujemy inne wyjątki
            errorMessage = "Nie udało się załadować planu zajęć: ${e.message}"
            dataLoadedSuccessfully = false
        } finally {
            isLoading = false
        }
    }

    val filteredSubjects = schedules.filter { it.day == selectedDay.toString() }
    val currentTime = LocalTime.now() // Bierzemy aktualny czas
    val currentDate = LocalDate.now() // Bierzemy dzisiejszą datę

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Plan Zajęć", style = MaterialTheme.typography.headlineLarge)

        // Pasek z dniami miesiąca
        DaysOfMonthScrollBar(displayedDays, selectedDay) { day ->
            selectedDay = day // Ustawienie wybranego dnia
        }

        // Wyświetlanie daty i zajęć na wybrany dzień
        Text(
            text = "Schedule for ${selectedDay.format(DateTimeFormatter.ofPattern("EEEE dd.MM.yyyy"))}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )

        // Komunikat o błędzie
        errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(16.dp))
        }

        // Jeśli wystąpił błąd lub dane się ładują, nie pokazuj komunikatu "No classes"
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                )
            }
        } else if (!dataLoadedSuccessfully) {
            // Jeśli wystąpił błąd, nie pokazuj komunikatu o braku zajęć
            return@Column
        } else if (filteredSubjects.isEmpty()) {
            Text("No classes for this day", modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(filteredSubjects) { subject ->
                    // Zmieniamy wygląd trwającej lekcji
                    val isCurrentLesson = isLessonNow(subject, currentTime, currentDate)
                    SubjectCard(subject, isCurrentLesson)  // Przekazujemy flagę do komponentu
                }
            }
        }
    }
}




@Composable
fun DaysOfMonthScrollBar(
    daysOfMonth: List<LocalDate>,
    selectedDay: LocalDate,
    onDaySelected: (LocalDate) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(daysOfMonth) { day ->
            DayItem(day, selectedDay) {
                onDaySelected(day) // Zmiana wybranego dnia
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun isLessonNow(schedule: Schedule, currentTime: LocalTime, currentDate: LocalDate): Boolean {
    try {
        // Usuń wiodące zero z godziny, aby był zawsze format 'h:mm a'
        val startTimeFormatted = removeLeadingZero(schedule.startTime)
        val endTimeFormatted = removeLeadingZero(schedule.endTime)

        // Konwertowanie godzin rozpoczęcia i zakończenia lekcji na obiekt LocalTime
        val startTime = LocalTime.parse(startTimeFormatted, DateTimeFormatter.ofPattern("h:mm a"))
        val endTime = LocalTime.parse(endTimeFormatted, DateTimeFormatter.ofPattern("h:mm a"))

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

@Composable
fun SubjectCard(schedule: Schedule, isCurrentLesson: Boolean) {
    // Define the background color and text color depending on whether it's the current lesson
    val backgroundColor = if (isCurrentLesson) Color(0xffff6200) else Color.White
    val textColor = if (isCurrentLesson) Color.White else Color.Black

    // Elevation for the card, based on whether it's the current lesson or not
    val cardElevation = if (isCurrentLesson) 8.dp else 2.dp

    // Apply the card modifier, elevation, and background color
    Card(
        modifier = Modifier
            .fillMaxWidth() // Make the card fill the full width of its parent
            .padding(vertical = 8.dp), // Apply padding around the card
        elevation = CardDefaults.cardElevation(cardElevation),
        colors = CardDefaults.cardColors(backgroundColor) // Apply background color
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = schedule.subject, style = MaterialTheme.typography.bodyLarge, color = textColor, fontWeight = FontWeight.ExtraBold )
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
            Text(text = "Time: ${schedule.startTime} - ${schedule.endTime}", color = textColor)
            Text(text = "Day: ${schedule.day}", color = textColor)
            Text(text = "Classroom: ${schedule.classroom}", color = textColor)
            Text(text = "Teacher: ${schedule.teacher}", color = textColor)
        }
    }
}
