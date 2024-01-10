package com.example.trip

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Event::class), version = 1, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: EventDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): EventDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "event_database"
                ).addCallback(EventDatabaseCallback(scope)).build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class EventDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.eventDao())
                }
            }
        }

        suspend fun populateDatabase(eventDao: EventDao) {

            var event = Event(
                title = "Berlin",
                notes = "Berlin",
                location = "Germany",
                startDateTime = "2020-03-01",
                endDateTime = "2020-03-05",
                duration = "4 days",
                favorite = true,
                mood = Utils.TravelMood.Medium,
                type = Utils.TravelType.Family
            )
            eventDao.insert(event)
        }
    }
}