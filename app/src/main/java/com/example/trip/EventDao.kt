package com.example.trip

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM event_table ORDER BY _id DESC")
    fun getAlphabetizedEvents(): Flow<List<Event>>

    @Insert
    suspend fun insert(event: Event)

    @Query("DELETE FROM event_table")
    fun deleteAll()

    @Query("Update event_table SET favorite = :favorite WHERE _id = :id")
    suspend fun updateFavorite(id: Int, favorite: Boolean)

    @Query("SELECT * FROM event_table WHERE favorite = 1 ORDER BY _id DESC")
    fun getFavoriteEvents(): Flow<List<Event>>

    @Update
    suspend fun updateEvent(event: Event)
}