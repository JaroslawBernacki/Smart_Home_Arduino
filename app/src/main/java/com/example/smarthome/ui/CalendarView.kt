package com.example.smarthome.ui

import android.widget.CalendarView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.smarthome.NoteViewModel

@Composable
fun CalendarViewScreen(viewModel:NoteViewModel) {
    var selectedDate by remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Kalendarz Produktywności", style = MaterialTheme.typography.headlineLarge)
        AndroidView(
            factory = { context ->
                CalendarView(context).apply {
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        val formattedMonth = String.format("%02d", month + 1) // Ensure two-digit month
                        val formattedDay = String.format("%02d", dayOfMonth) // Ensure two-digit day
                        selectedDate = "$year-$formattedMonth-$formattedDay"
                    }
                }
            },
            modifier = Modifier.padding(16.dp)
        )

        Text(text = "Wybrana data: $selectedDate")

        Button(onClick = {
            val mood = viewModel.getMoodForDate(selectedDate)
            selectedDate = "$selectedDate: $mood"
        }) {
            Text("Sprawdź")
        }
    }
}


