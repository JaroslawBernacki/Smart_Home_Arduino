package com.example.smarthome

import androidx.lifecycle.LiveData

class NotesRepository(private val noteEntryDao: NoteEntryDao) {

    val allEntries: LiveData<List<NoteEntry>> = noteEntryDao.getAllEntries()

    suspend fun insert(entry: NoteEntry) {
        noteEntryDao.insert(entry)
    }

    suspend fun delete(entry: NoteEntry) {
        noteEntryDao.delete(entry)
    }

    suspend fun update(entry: NoteEntry) {
        noteEntryDao.update(entry)
    }

    /*fun getFilteredEntries(mood: Int): LiveData<List<NoteEntry>> {
        return noteEntryDao.getFilteredEntries(mood)
    }*/
}
