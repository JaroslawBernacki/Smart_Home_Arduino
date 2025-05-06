package com.example.smarthome.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smarthome.NoteViewModel
import com.example.smarthome.NoteEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import java.io.OutputStream
import java.util.*

@Composable
fun MoodEntryScreen(viewModel: NoteViewModel) {
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    var socket: BluetoothSocket? = null
    var outputStream: OutputStream? = null

    val deviceName = "ESP32-BT-Slave"
    val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Standardowy SPP UUID
    var selectedMood by remember { mutableStateOf(3) }  // Default mood
    var description by remember { mutableStateOf("") }
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    var isEditing by remember { mutableStateOf(false) }
    var currentEntryId by remember { mutableStateOf<Long?>(null) }
    val moodEntries = viewModel.allEntries.observeAsState(emptyList())

    var filterMood by remember { mutableStateOf("All") }
    var filterDate by remember { mutableStateOf("") }

    val filteredEntries = moodEntries.value.filter {
        //(filterMood == "All" || it.mood.toString() == filterMood) &&
                (filterDate.isEmpty() || it.date == filterDate)
    }

    fun startEditing(entry: NoteEntry) {
        //selectedMood = entry.mood
        description = entry.description.toString()
        currentEntryId = entry.id
        isEditing = true
    }

    fun saveEntry() {
        if (currentEntryId != null) {
            val updatedEntry = NoteEntry(
                id = currentEntryId!!,
                date = currentDate,
                //mood = selectedMood.toInt(),
                description = description
            )
            viewModel.update(updatedEntry)
            isEditing = false
            currentEntryId = null
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Moje Notatki", style = MaterialTheme.typography.headlineLarge)

        //Text("Wybierz poziom produktywności: ${selectedMood.toInt()}", style = MaterialTheme.typography.bodyLarge)

        /*Slider(
            value = selectedMood.toFloat(),
            onValueChange = { selectedMood = it.toInt() },
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier.fillMaxWidth()
        )*/

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Opis") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isEditing) {
                    saveEntry()
                } else {
                    val entry = NoteEntry(
                        date = currentDate,
                        //mood = selectedMood,
                        description = description
                    )
                    //sampleEntries.forEach { viewModel.insert(it) }
                    viewModel.insert(entry)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Zapisz zmiany" else "Dodaj wpis")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }) {
                    Text("Produktywność: $filterMood")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("All", "1", "2", "3", "4", "5").forEach { mood ->
                        DropdownMenuItem(text = { Text(mood) }, onClick = {
                            filterMood = mood
                            expanded = false
                        })
                    }
                }
            }


            TextField(
                value = filterDate,
                onValueChange = { filterDate = it },
                label = { Text("Filtrowanie datą (YYYY-MM-DD)") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(filteredEntries) { moodEntry ->
                MoodItem(
                    noteEntry = moodEntry,
                    onDelete = { viewModel.delete(it) },
                    onEdit = { startEditing(it) }
                )
            }
        }
    }
}

