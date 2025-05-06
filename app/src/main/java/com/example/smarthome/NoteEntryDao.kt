package com.example.smarthome

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteEntryDao {
    @Insert
    suspend fun insert(entry: NoteEntry)

    @Update
    suspend fun update(entry: NoteEntry)

    @Delete
    suspend fun delete(entry: NoteEntry)

    @Query("SELECT * FROM mood_entries ORDER BY date DESC")
    fun getAllEntries(): LiveData<List<NoteEntry>>

    //@Query("SELECT * FROM mood_entries WHERE mood = :mood ORDER BY date DESC")
    //fun getFilteredEntries(mood: Int): LiveData<List<NoteEntry>>
}
