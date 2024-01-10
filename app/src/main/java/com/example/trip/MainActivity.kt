package com.example.trip

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory((application as EventsApplication).repository)
    }

    private val newEventActivityRequestCode = 1
    private val updateEventActivityRequestCode = 2

    lateinit var binding : ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle

    val CITY: String = "bucharest,ro"
    val API: String = "7933c2442046308468205094cb11a2d1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open_drawer, R.string.close_drawer)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            navView.setCheckedItem(R.id.nav_home)

            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_about -> {
                        val intent = Intent(this@MainActivity, MainActivity2::class.java)
                        startActivity(intent)
                    }
                    R.id.nav_settings -> {
                        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.nav_contact -> {
                        Toast.makeText(this@MainActivity, "Contact unavailable", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
        }
        weatherTask().execute()
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

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").
                readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result!!)
                val main = jsonObj.getJSONObject("main")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val temp = main.getString("temp")+"°C"
                val weatherDescription = weather.getString("main")

                binding.temperatura.text = temp
                binding.vreme.text = weatherDescription

            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
            }

        }
    }

}