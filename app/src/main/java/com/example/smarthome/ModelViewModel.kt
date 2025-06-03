package com.example.smarthome
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.UUID

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

    @OptIn(UnstableApi::class)
    fun sendNoteToESP32(noteText: String, context: Context) {
        val permission = android.Manifest.permission.BLUETOOTH_CONNECT
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(permission), 1)
            return
        }

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val device = bluetoothAdapter?.bondedDevices?.find { it.name == "ESP32-BT-Slave" }

        if (device != null) {
            val uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb") // klasyczny SPP UUID
            Thread {
                try {
                    val socket = device.createRfcommSocketToServiceRecord(uuid)
                    socket.connect()
                    socket.outputStream.write(noteText.toByteArray())
                    socket.close()
                } catch (e: Exception) {
                    Log.e("Bluetooth", "Błąd wysyłania: ${e.message}")
                }
            }.start()
        } else {
            Toast.makeText(context, "ESP32-slave nie znaleziony", Toast.LENGTH_SHORT).show()
        }
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
    /*fun startBluetoothListening() {
        BluetoothManager.startListening { value ->
            viewModelScope.launch {
                repository.insert(WindEntry(speed = value, timestamp = System.currentTimeMillis()))
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
