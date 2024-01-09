package com.example.trip

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_table")
data class Event(
    @PrimaryKey(autoGenerate = true) val _id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "startDate")var startDateTime: String,
    @ColumnInfo(name = "endDate")var endDateTime: String,
    @ColumnInfo(name = "notes")var notes: String,
    @ColumnInfo(name = "location")var location: String,
    @ColumnInfo(name = "duration")var duration: String,
    @ColumnInfo(name = "favorite")var favorite: Boolean,
    @ColumnInfo(name = "mood")var mood: Utils.TravelMood,
    @ColumnInfo(name = "type")var type: Utils.TravelType
)