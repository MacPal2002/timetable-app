package com.example.timetable.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun StateMessage(
    message: String, // Tekst komunikatu
    icon: (@Composable () -> Unit)? = null, // Opcjonalna ikona
    buttonText: String? = null, // Opcjonalny tekst przycisku
    onButtonClick: (() -> Unit)? = null, // Opcjonalna akcja po kliknięciu przycisku
    modifier: Modifier = Modifier // Możliwość dostosowania modyfikatora
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ikona (jeśli jest dostępna)
        icon?.let {
            it() // Wywołanie composable ikony
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tekst komunikatu
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        // Przycisk (jeśli jest dostępny)
        buttonText?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onButtonClick?.invoke() }) {
                Text(text = it)
            }
        }
    }
}
