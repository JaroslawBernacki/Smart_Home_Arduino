package com.example.smarthome.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarthome.NoteEntry
import com.example.smarthome.BluetoothManager.sendData

@Composable
fun MoodItem(noteEntry: NoteEntry, onDelete: (NoteEntry) -> Unit, onEdit: (NoteEntry) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = noteEntry.date,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )

                /*Text(
                    text = getMoodIcon(noteEntry.mood),
                    style = TextStyle(fontSize = 24.sp),
                    modifier = Modifier.padding(vertical = 8.dp)
                )*/

                noteEntry.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = { onEdit(noteEntry) }) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onDelete(noteEntry) }) {
                    Text("Delete")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { noteEntry.description?.let { sendData(noteEntry.description) } }) {
                    Text("Wyślij")
                }
            }
        }
    }
}
/*
@Composable
fun getMoodIcon(mood: Int): String {
    return when (mood) {
        1 -> "😞"
        2 -> "🙁"
        3 -> "😐"
        4 -> "🙂"
        5 -> "😊"
        else -> "😐"
    }
}*/