package com.example.trip

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow


class EventRepository (private val eventDao: EventDao) {

    val allEvents: Flow<List<Event>> = eventDao.getAlphabetizedEvents()

    @WorkerThread
    suspend fun insert(event: Event) {
        eventDao.insert(event)
    }
}