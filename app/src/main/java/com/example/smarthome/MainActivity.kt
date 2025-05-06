package com.example.smarthome

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smarthome.BluetoothManager.connectToDevice
import com.example.smarthome.ui.CalendarViewScreen
import com.example.smarthome.ui.WindChartScreen
import com.example.smarthome.ui.MoodEntryScreen
import com.example.smarthome.ui.RetryScreen
import java.io.OutputStream
import java.util.UUID
import android.Manifest
import androidx.compose.ui.platform.LocalContext
//import com.example.smarthome.BluetoothManager.bluetoothAdapter
import com.example.smarthome.BluetoothManager.startListening

import com.example.smarthome.ui.CurrentWindSpeedScreen

class MainActivity : ComponentActivity() {

    private val notesViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((application as NotesApplication).repository)
    }

    private val windViewModel: WindViewModel by viewModels {
        WindViewModelFactory((application as NotesApplication).windRepository)
    }

    private val deviceName = "ESP32-anemometer-Slave"
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ⚠️ Upewnij się, że masz uprawnienia
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
        }

        setContent {
            val context = LocalContext.current
            var isConnected by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                isConnected = BluetoothManager.connectToDevice(
                    deviceName = deviceName,
                    uuid = uuid,
                    context = context
                )

            }

            if (isConnected) {
                SmarthomeApp(notesViewModel, windViewModel)
                startListening(windViewModel)
            } else {
                RetryScreen {
                    isConnected = BluetoothManager.connectToDevice(
                        deviceName = deviceName,
                        uuid = uuid,
                        context = context
                    )

                }
            }
        }
    }
}
/*
class MainActivity : ComponentActivity() {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    private val ViewModelNotes: NoteViewModel by viewModels {
        NoteViewModelFactory((application as NotesApplication).repository)
    }
    private val ViewModelWind: WindViewModel by viewModels {
        WindViewModelFactory((application as WindApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            SmarthomeApp(ViewModelNotes, ViewModelWind)

            // Automatyczne łączenie po załadowaniu UI
            LaunchedEffect(Unit) {
                connectToDevice(
                    deviceName = "ESP32-anemometer-Slave",
                    uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"),
                    context = this@MainActivity
                )
            }
        }
    }

}*//*
class MainActivity : ComponentActivity() {
    //private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    //private var socket: BluetoothSocket? = null
    //private var outputStream: OutputStream? = null

    private val ViewModelNotes: NoteViewModel by viewModels {
        NoteViewModelFactory((application as NotesApplication).repository)
    }
    private val ViewModelWind: WindViewModel by viewModels {
        WindViewModelFactory((application as WindApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //val success = BluetoothManager.connectToDevice(deviceName = "ESP32-anemometer-Slave",
        //    uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"),
        //    context = this)
        //val windViewModel: WindViewModel by viewModels {
        //    WindViewModelFactory((application as WindApplication).repository)
        //}
        //windViewModel.startListening(socket?.inputStream!!)
        setContent {
            SmarthomeApp(ViewModelNotes,ViewModelWind)
            //LaunchedEffect(Unit) {
            //    connectToDevice(
            //        deviceName = "ESP32-anemometer-Slave",
            //        uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"),
            //        context = this@MainActivity
            //    )
            //}
            /*if (success) {
                SmarthomeApp(ViewModelNotes,ViewModelWind)
            } else {
                RetryScreen {
                    // ponowna próba
                    if (BluetoothManager.connectToDevice(    deviceName = "ESP32-anemometer-Slave",
                            uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"),
                            context = this)) {
                        // Rebuild composable UI

                        setContent {
                            SmarthomeApp(ViewModelNotes,ViewModelWind)

                        }
                    }
                }
            }*/
        }

    }

}*/
@Composable
fun SmarthomeApp(viewModel:NoteViewModel,windmodel:WindViewModel) {
    val navController = rememberNavController()
    Log.d("DEBUG", "MainScreen Composable Loaded") // Debugging log

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = "mood_entry"
            ) {
                composable("mood_entry") {
                    Log.d("DEBUG", "Navigating to MoodTrackerScreen")
                    MoodEntryScreen(viewModel)
                }
                composable("calendar_view") {
                    Log.d("DEBUG", "Navigating to MoodCalendarView")
                    CalendarViewScreen(viewModel)
                }
                composable("chart_view") {
                    Log.d("DEBUG", "Navigating to Chart")
                    WindChartScreen(windmodel)
                }
                composable("weather_view") {
                    Log.d("DEBUG", "Navigating to Weather")
                    CurrentWindSpeedScreen(windmodel)
                }
            }
        }
    }


}





/*
package com.example.smarthome;
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.smarthome.R
import java.io.OutputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    private val deviceName = "ESP32-BT-Slave"
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Standardowy SPP UUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnConnect = findViewById<Button>(R.id.btnConnect)
        val btn1= findViewById<Button>(R.id.btnImg1)
        val btn2= findViewById<Button>(R.id.btnImg2)
        val btn3= findViewById<Button>(R.id.btnImg3)
        val btn4= findViewById<Button>(R.id.btnImg4)
        val btn5= findViewById<Button>(R.id.btnImg5)
        val editTextMessage = findViewById<EditText>(R.id.editTextMessage)
        btnConnect.setOnClickListener {
            connectToDevice()
            btn1.isEnabled = true
            btn2.isEnabled = true
            btn3.isEnabled = true
            btn4.isEnabled = true
            btn5.isEnabled = true
        }

        btn1.setOnClickListener {
            sendData("Image1\n")
        }
        btn2.setOnClickListener {
            sendData("Image2\n")
        }
        btn3.setOnClickListener {
            sendData("Image3\n")
        }
        btn4.setOnClickListener {
            sendData("Image4\n")
        }
        btn5.setOnClickListener {
            val text = editTextMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendData("$text\n")
            } else {
                Toast.makeText(this, "Pole tekstowe jest puste", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun connectToDevice() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth nie jest dostępny", Toast.LENGTH_SHORT).show()
            return
        }

        if (!bluetoothAdapter!!.isEnabled) {
            Toast.makeText(this, "Włącz Bluetooth", Toast.LENGTH_SHORT).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
            return
        }

        val device: BluetoothDevice? = bluetoothAdapter!!.bondedDevices.find {
            it.name == deviceName
        }

        if (device == null) {
            Toast.makeText(this, "Nie znaleziono urządzenia", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            socket = device.createRfcommSocketToServiceRecord(uuid)
            socket?.connect()
            outputStream = socket?.outputStream
            Toast.makeText(this, "Połączono z ${device.name}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Błąd połączenia: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun sendData(data: String) {
        try {
            outputStream?.write(data.toByteArray())
            Toast.makeText(this, "Dane wysłane", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Błąd wysyłania", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socket?.close()
    }
}
*/