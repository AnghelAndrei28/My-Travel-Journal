package com.example.trip

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Update
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory((application as EventsApplication).repository)
    }

    private val newEventActivityRequestCode = 1
    private val updateEventActivityRequestCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = EventListAdapter(
            listener = { event -> updateEvent(event) },
            favoriteListener = { event -> eventViewModel.updateFavorite(event._id, !event.favorite)
            })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val eventsCounter = findViewById<TextView>(R.id.events_counter)
        val favoriteSwitch =
            findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.favorite_switch)
        eventViewModel.allEvents.observe(this, Observer { events ->
            events?.let {
                adapter.submitList(it)
                eventsCounter.text = "Number of trips: ${it.size}"
            }
        })
        favoriteSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                eventViewModel.favoriteEvents.observe(this, Observer { events ->
                    events?.let {
                        adapter.submitList(it)
                        eventsCounter.text = "Number of trips: ${it.size}"
                    }
                })
            } else {
                eventViewModel.allEvents.observe(this, Observer { events ->
                    events?.let {
                        adapter.submitList(it)
                        eventsCounter.text = "Number of trips: ${it.size}"
                    }
                })
            }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewEventActivity::class.java)
            startActivityForResult(intent, newEventActivityRequestCode)
        }
    }

    fun updateEvent(event: Event) {
        val intent = Intent(this@MainActivity, NewEventActivity::class.java)
        intent.putExtra("event", event)
        startActivityForResult(intent, updateEventActivityRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newEventActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getSerializableExtra(NewEventActivity.EXTRA_REPLY)?.let {
                eventViewModel.insert(event = it as Event)
            }
        } else if (requestCode == updateEventActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getSerializableExtra(NewEventActivity.EXTRA_REPLY)?.let {
                eventViewModel.updateEvent(event = it as Event)
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}