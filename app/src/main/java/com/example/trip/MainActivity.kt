package com.example.trip

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Update
import com.example.trip.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory((application as EventsApplication).repository)
    }

    private val newEventActivityRequestCode = 1
    private val updateEventActivityRequestCode = 2

    lateinit var binding : ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open_drawer, R.string.close_drawer)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.profileFragment -> {
                        Toast.makeText(this@MainActivity, "First Item Clicked", Toast.LENGTH_SHORT).show()
                    }
                    R.id.aboutFragment -> {
                        Toast.makeText(this@MainActivity, "Second Item Clicked", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }
}