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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory((application as EventsApplication).repository)
    }

    private val newEventActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = EventListAdapter(eventViewModel)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newEventActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getSerializableExtra(NewEventActivity.EXTRA_REPLY)?.let {
                val event = Gson().fromJson(it.toString(), Event::class.java)
                eventViewModel.insert(event)
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