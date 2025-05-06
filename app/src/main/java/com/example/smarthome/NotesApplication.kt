package com.example.smarthome

import android.app.Application

class NotesApplication : Application() {
    val repository: NotesRepository
        get() = NotesRepository(NoteDatabase.getDatabase(this).noteEntryDao())
    val windRepository: WindRepository
        get() = WindRepository(NoteDatabase.getDatabase(this).windEntryDao())

}

