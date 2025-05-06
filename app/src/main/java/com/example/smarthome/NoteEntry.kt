package com.example.smarthome

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class NoteEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    //val mood: Int,
    val description: String?
)
