package com.example.trip

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EventViewModel (private val repository: EventRepository) : ViewModel() {
    val allEvents: LiveData<List<Event>> = repository.allEvents.asLiveData()

    fun insert(event: Event) {
        viewModelScope.launch {
            repository.insert(event)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun updateFavorite(id: Int, favorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(id, favorite)
        }
    }
}

class EventViewModelFactory(private val repository: EventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}