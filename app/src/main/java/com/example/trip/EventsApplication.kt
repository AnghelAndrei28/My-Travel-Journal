package com.example.trip

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class EventsApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { EventDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { EventRepository(database.eventDao()) }
}