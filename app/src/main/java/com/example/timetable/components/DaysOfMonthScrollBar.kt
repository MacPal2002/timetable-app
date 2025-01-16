import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timetable.components.DayItem
import java.time.LocalDate

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