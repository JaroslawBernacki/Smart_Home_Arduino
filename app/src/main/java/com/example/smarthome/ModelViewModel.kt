package com.example.smarthome

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStream

class NoteViewModel(private val repository: NotesRepository) : ViewModel() {
    val allEntries: LiveData<List<NoteEntry>> = repository.allEntries
    fun insert(entry: NoteEntry) {
        viewModelScope.launch {
            repository.insert(entry)
        }
    }
    fun delete(entry: NoteEntry) {
        viewModelScope.launch {
            repository.delete(entry)
        }
    }
    fun update(entry: NoteEntry) {
        viewModelScope.launch {
            repository.update(entry)
        }
    }
    fun getMoodForDate(date: String): String {
        val entry = allEntries.value?.find { it.date == date }
        return entry?.let { "Notatka: ${it.description}" } ?: "Brak notatki tego dnia"
        //return entry?.let { "Produktywność: ${it.mood}, Opis: ${it.description}" } ?: "No mood entry for this date"
    }
}
class WindViewModel(private val repository: WindRepository) : ViewModel() {
    val allEntries: Flow<List<WindEntry>> = repository.allWindEntries
    val windSpeed: StateFlow<Float> get() = repository.windSpeed
    fun addWindData(speed: Float) {
        val wind = WindEntry(
            speed = speed,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.insert(wind)
        }
    }

    /*fun startListening(inputStream: InputStream) {
        viewModelScope.launch {
            try {
                val buffer = ByteArray(1024)
                while (true) {
                    val bytes = inputStream.read(buffer)
                    if (bytes > 0) {
                        val received = String(buffer, 0, bytes).trim()
                        val speed = received.toFloatOrNull()
                        speed?.let {
                            repository.updateWindSpeed(it)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }*/
}


/*fun readWindDataOnce(inputStream: InputStream, viewModel: WindViewModel, context: Context) {
    try {
        val buffer = ByteArray(1024)
        val bytes = inputStream.read(buffer)  // zablokuje wątek dopóki nie dostanie danych
        val incoming = String(buffer, 0, bytes).trim()
        val speed = incoming.toFloatOrNull()

        if (speed != null) {
            viewModel.addWindEntry(speed)
            Toast.makeText(context, "Odebrano: $speed m/s", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Nieprawidłowe dane: $incoming", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Błąd odczytu: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}*/
class WindViewModelFactory(private val repository: WindRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WindViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WindViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class NoteViewModelFactory(private val repository: NotesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
