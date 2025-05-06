package com.example.smarthome
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import java.io.OutputStream
import java.util.UUID
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.OptIn
import androidx.core.app.ActivityCompat
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import java.io.IOException
import java.io.InputStream
import kotlin.concurrent.thread

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
