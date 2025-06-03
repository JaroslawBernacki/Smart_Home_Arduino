package com.example.smarthome
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import java.io.OutputStream
import java.util.UUID
import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.OptIn
import androidx.core.app.ActivityCompat
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import android.util.Log
import kotlin.concurrent.thread

object BluetoothReceiverManager {
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null
    private var inputStream: InputStream? = null

    fun connect(context: Context, deviceName: String, uuid: UUID): Boolean {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
            return false
        }

        val device = bluetoothAdapter?.bondedDevices?.find { it.name == deviceName } ?: return false

        return try {
            socket = device.createRfcommSocketToServiceRecord(uuid).apply { connect() }
            inputStream = socket?.inputStream
            true
        } catch (e: IOException) {
            Log.e("BluetoothReceiver", "Connection error", e)
            false
        }
    }
    fun startListening(onDataReceived: (Float) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val buffer = ByteArray(1024)
            var leftover = "" // bufor na niedokończoną wiadomość

            try {
                while (true) {
                    val bytes = inputStream?.read(buffer) ?: break
                    val chunk = String(buffer, 0, bytes)
                    leftover += chunk

                    // Rozbij na linie (zakładamy, że wiadomości kończą się \n lub \r\n)
                    val lines = leftover.split("\n")

                    // Wszystko poza ostatnim elementem traktujemy jako kompletne wiadomości
                    for (i in 0 until lines.size - 1) {
                        val message = lines[i].trim()
                        Log.d("BluetoothReceiver", "Odebrano pełne: '$message'")
                        message.toFloatOrNull()?.let {
                            onDataReceived(it)
                        } ?: Log.w("BluetoothReceiver", "Nieprawidłowa wartość: '$message'")
                    }

                    // Ostatni fragment (może być niepełny) zapisz jako nowy `leftover`
                    leftover = lines.last()
                }
            } catch (e: IOException) {
                Log.e("BluetoothReceiver", "Błąd odczytu", e)
            }
        }
    }

/*    fun startListening(onDataReceived: (Float) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val buffer = ByteArray(1024)
                while (true) {
                    val bytes = inputStream?.read(buffer) ?: break
                    val message = String(buffer, 0, bytes).trim()
                    Log.d("BluetoothReceiver", "Odebrano: $message")
                    message.toFloatOrNull()?.let { onDataReceived(it) }
                }
            } catch (e: IOException) {
                Log.e("BluetoothReceiver", "Błąd odczytu", e)
            }
        }
    }
*/
    fun close() {
        inputStream?.close()
        socket?.close()
        inputStream = null
        socket = null
    }
}



object BluetoothSenderManager {
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    fun connect(context: Context, deviceName: String, uuid: UUID): Boolean {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
            return false
        }

        val device = bluetoothAdapter?.bondedDevices?.find { it.name == deviceName } ?: return false

        return try {
            socket = device.createRfcommSocketToServiceRecord(uuid).apply { connect() }
            outputStream = socket?.outputStream
            true
        } catch (e: IOException) {
            android.util.Log.d("BluetoothSender", "Connection error", e)
            false
        }
    }

    fun sendData(data: String) {
        try {
            android.util.Log.d("BluetoothSender","Wyslano wiadomosc")
            outputStream?.write(data.toByteArray())
        } catch (e: IOException) {
            android.util.Log.d("BluetoothSender", "Błąd wysyłania", e)
        }
    }

    fun close() {
        outputStream?.close()
        socket?.close()
        outputStream = null
        socket = null
    }
}


/*
object BluetoothManager {
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    var inputStream: InputStream? = null
    val isConnected: Boolean
        get() = socket?.isConnected == true
    fun connectToDevice(deviceName: String, uuid: UUID, context: Context): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
            return false
        }

        val device = bluetoothAdapter?.bondedDevices?.find { it.name == deviceName }
            ?: return false

        return try {
            val socket = device.createRfcommSocketToServiceRecord(uuid)
            socket.connect()
            outputStream = socket.outputStream
            inputStream=socket?.inputStream
            this.socket = socket
            true
        } catch (e: SecurityException) {
            e.printStackTrace()
            false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }


    fun sendData(data: String) {
        try {
            outputStream?.write(data.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun closeConnection() {
        outputStream?.close()
        socket?.close()
        outputStream = null
        socket = null
    }

    @OptIn(UnstableApi::class)
    fun startListening(windViewModel: WindViewModel) {
        // Sprawdź, czy inputStream jest poprawnie ustawiony
        inputStream?.let { stream ->
            // Tworzenie nowego wątku, który odbiera dane z urządzenia Bluetooth
            thread {
                val buffer = ByteArray(1024)
                val stringBuilder = StringBuilder()

                while (true) {
                    try {
                        val bytes = inputStream!!.read(buffer)
                        val part = String(buffer, 0, bytes)

                        Log.d("Bluetooth", "Odebrano: $part")

                        stringBuilder.append(part)

                        if (stringBuilder.length >= 4) { // zakładamy np. "0.00"
                            val fullValue = stringBuilder.toString().trim()
                            stringBuilder.clear()

                            val windSpeed = fullValue.toFloatOrNull()
                            if (windSpeed != null) {
                                Log.d("Bluetooth", "Zapisuję do bazy: $windSpeed")
                                windViewModel.addWindData(windSpeed)
                            } else {
                                Log.e("Bluetooth", "Nieprawidłowa liczba: $fullValue")
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        break
                    }
                }

            }
        } ?: run {
            Log.e("Bluetooth", "Brak strumienia wejściowego!")
        }
    }
}
*/
/*
object AnemometerBluetooth {
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    var inputStream: InputStream? = null

    fun connect(context: Context): Boolean {
        return connectTo("ESP32-anemometer-Slave", context)
    }

    private fun connectTo(deviceName: String, context: Context): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val device = adapter?.bondedDevices?.find { it.name == deviceName } ?: return false
        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        return try {
            val socket = device.createRfcommSocketToServiceRecord(uuid)
            socket.connect()
            this.socket = socket
            this.outputStream = socket.outputStream
            this.inputStream = socket.inputStream
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun startListening(windViewModel: WindViewModel) {
        inputStream?.let { stream ->
            thread {
                val buffer = ByteArray(1024)
                val sb = StringBuilder()
                while (true) {
                    try {
                        val bytes = stream.read(buffer)
                        val part = String(buffer, 0, bytes)
                        sb.append(part)

                        if (sb.length >= 3) {
                            val value = sb.toString().trim()
                            sb.clear()
                            value.toFloatOrNull()?.let {
                                windViewModel.addWindData(it)
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        break
                    }
                }
            }
        }
    }
}
object NoteSenderBluetooth {
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    fun connect(context: Context): Boolean {
        return connectTo("ESP32-slave", context)
    }

    private fun connectTo(deviceName: String, context: Context): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val device = adapter?.bondedDevices?.find { it.name == deviceName } ?: return false
        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        return try {
            val socket = device.createRfcommSocketToServiceRecord(uuid)
            socket.connect()
            this.socket = socket
            this.outputStream = socket.outputStream
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun send(note: String) {
        try {
            outputStream?.write(note.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
*/
/*
object BluetoothManager {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private var windSocket: BluetoothSocket? = null
    private var noteSocket: BluetoothSocket? = null

    private var windOutputStream: OutputStream? = null
    private var noteOutputStream: OutputStream? = null

    fun connectToDevice(deviceName: String, uuid: UUID, context: Context): BluetoothSocket? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
            return null
        }

        val device = bluetoothAdapter?.bondedDevices?.find { it.name == deviceName } ?: return null

        return try {
            val socket = device.createRfcommSocketToServiceRecord(uuid)
            socket.connect()
            socket
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun initializeConnections(context: Context): Boolean {
        val windUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        val noteUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        windSocket = connectToDevice("ESP32-anemometer-Slave", windUUID, context)
        noteSocket = connectToDevice("ESP32-BT-slave", noteUUID, context)

        windOutputStream = windSocket?.outputStream
        noteOutputStream = noteSocket?.outputStream

        return windSocket?.isConnected == true && noteSocket?.isConnected == true
    }

    @OptIn(UnstableApi::class)
    fun sendNoteToESP(note: String) {
        try {
            noteOutputStream?.write((note + "\n").toByteArray())
        } catch (e: Exception) {
            Log.e("Bluetooth", "Błąd wysyłania do ESP32-slave: ${e.message}")
        }
    }

    fun closeConnections() {
        windOutputStream?.close()
        windSocket?.close()
        noteOutputStream?.close()
        noteSocket?.close()
    }

    fun getWindInputStream(): InputStream? = windSocket?.inputStream

    fun startListening(viewModel: WindViewModel) {
        val inputStream = socket?.inputStream ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val buffer = ByteArray(1024)
            while (true) {
                try {
                    val bytesRead = inputStream.read(buffer)
                    if (bytesRead > 0) {
                        val message = String(buffer, 0, bytesRead).trim()
                        Log.d("Bluetooth", "Odebrano: $message")
                        val speed = message.toFloatOrNull()
                        if (speed != null) {
                            viewModel.insertWindSpeed(speed)
                        }
                    }
                } catch (e: IOException) {
                    Log.e("Bluetooth", "Błąd odbierania danych", e)
                    break
                }
            }
        }
    }

}
*//*
object BluetoothManager {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private var socketAnemometer: BluetoothSocket? = null
    private var inputStreamAnemometer: InputStream? = null

    private var socketNote: BluetoothSocket? = null
    private var outputStreamNote: OutputStream? = null

    fun connectAnemometer(deviceName: String, uuid: UUID, context: Context): Boolean {
        return connect(deviceName, uuid, context) { socket ->
            socketAnemometer = socket
            inputStreamAnemometer = socket.inputStream
        }
    }

    fun connectNoteDevice(deviceName: String, uuid: UUID, context: Context): Boolean {
        return connect(deviceName, uuid, context) { socket ->
            socketNote = socket
            outputStreamNote = socket.outputStream
        }
    }

    @OptIn(UnstableApi::class)
    private fun connect(
        deviceName: String,
        uuid: UUID,
        context: Context,
        onConnected: (BluetoothSocket) -> Unit
    ): Boolean {
        if (bluetoothAdapter == null) return false

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
            return false
        }

        val device: BluetoothDevice = bluetoothAdapter.bondedDevices.find { it.name == deviceName }
            ?: return false

        return try {
            val socket = device.createRfcommSocketToServiceRecord(uuid)
            socket.connect()
            onConnected(socket)
            true
        } catch (e: Exception) {
            Log.e("Bluetooth", "Connection error: ${e.message}")
            false
        }
    }

    fun getAnemometerInputStream(): InputStream? = inputStreamAnemometer

    @OptIn(UnstableApi::class)
    fun sendNote(text: String) {
        try {
            outputStreamNote?.write(text.toByteArray())
        } catch (e: Exception) {
            Log.e("Bluetooth", "Błąd wysyłania: ${e.message}")
        }
    }

    fun closeConnections() {
        inputStreamAnemometer?.close()
        socketAnemometer?.close()

        outputStreamNote?.close()
        socketNote?.close()
    }

    @OptIn(UnstableApi::class)
    fun startListening(onDataReceived: (Float) -> Unit) {
        val inputStream = socketAnemometer?.inputStream ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val buffer = ByteArray(1024)
                while (true) {
                    val bytesRead = inputStream.read(buffer)
                    if (bytesRead > 0) {
                        val data = String(buffer, 0, bytesRead).trim()
                        Log.d("Bluetooth", "Odebrano: $data")

                        data.toFloatOrNull()?.let { value ->
                            onDataReceived(value)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Bluetooth", "Błąd odczytu: ${e.message}")
            }
        }
    }

}*/
