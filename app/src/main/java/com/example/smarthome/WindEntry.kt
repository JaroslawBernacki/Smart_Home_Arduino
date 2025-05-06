package com.example.smarthome

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wind_data")
data class WindEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val speed: Float
)