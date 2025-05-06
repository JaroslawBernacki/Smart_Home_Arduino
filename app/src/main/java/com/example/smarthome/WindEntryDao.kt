package com.example.smarthome

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WindDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WindEntry)

    @Query("SELECT * FROM wind_data ORDER BY timestamp ASC")
    fun getAll(): Flow<List<WindEntry>>
}
