package com.example.trip

import android.os.AsyncTask
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow


class EventRepository (private val eventDao: EventDao) {

    val allEvents: Flow<List<Event>> = eventDao.getAlphabetizedEvents()
    val favoriteEvents: Flow<List<Event>> = eventDao.getFavoriteEvents()

    @WorkerThread
    suspend fun insert(event: Event) {
        eventDao.insert(event)
    }

    fun deleteAll() {
        deleteAllEventsAsyncTask(eventDao).execute()
    }

    private class deleteAllEventsAsyncTask constructor(dao: EventDao) :
        AsyncTask<Void?, Void?, Void?>() {
        private val mAsyncTaskDao: EventDao

        init {
            mAsyncTaskDao = dao
        }


        override fun doInBackground(vararg p0: Void?): Void? {
            mAsyncTaskDao.deleteAll()
            return null
        }
    }

    @WorkerThread
    suspend fun updateFavorite(id: Int, favorite: Boolean) {
        eventDao.updateFavorite(id, favorite)
    }
}