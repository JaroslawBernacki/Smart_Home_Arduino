package com.example.smarthome

import android.app.Application
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WindRepository(private val windDao: WindDao) {
    val allWindEntries: Flow<List<WindEntry>> = windDao.getAll()
    private val _windSpeed = MutableStateFlow(0f)
    val windSpeed: StateFlow<Float> get() = _windSpeed
    val windEntries = windDao.getAll().stateIn(
        CoroutineScope(Dispatchers.IO),
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    suspend fun insert(wind: WindEntry) {
        windDao.insert(wind)
    }
    fun updateWindSpeed(speed: Float) {
        _windSpeed.value = speed
        CoroutineScope(Dispatchers.IO).launch {
            windDao.insert(WindEntry(speed = speed))
        }
    }
}

/*class WindRepository(private val windDao: WindDao) {
    val allWindEntries: Flow<List<WindEntry>> = windDao.getAll()

    suspend fun insert(entry: WindEntry) {
        windDao.insert(entry)
    }
}*/
