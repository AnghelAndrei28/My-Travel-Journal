package com.example.trip

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.slider.Slider
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

class NewEventActivity : AppCompatActivity() {

    private lateinit var title: EditText
    lateinit var startDate: TextView
    lateinit var endDate: TextView
    lateinit var startDatePicker: Button
    lateinit var endDatePicker: Button
    lateinit var location: EditText
    lateinit var notes: EditText
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_event)

        title = findViewById(R.id.edit_title)
        location = findViewById(R.id.edit_location)
        startDate = findViewById(R.id.startDateText)
        endDate = findViewById(R.id.endDateText)
        notes = findViewById(R.id.edit_notes)
        startDatePicker = findViewById(R.id.startDate)
        endDatePicker = findViewById(R.id.endDate)

        startDatePicker.setOnClickListener {
            showDatePicker(startDate)
        }
        endDatePicker.setOnClickListener {
            showDatePicker(endDate)
        }

        val spinner = findViewById<android.widget.Spinner>(R.id.spinner)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, Utils.TravelType.values())
        spinner.adapter = adapter
        spinner.setSelection(0)
        var mood: Utils.TravelMood = Utils.TravelMood.Happy
        val slider = findViewById<Slider>(R.id.mood_slider)
        slider.addOnChangeListener { slider, value, fromUser ->
            mood = when (value.toInt()) {
                1 -> Utils.TravelMood.Happy
                2 -> Utils.TravelMood.Medium
                3 -> Utils.TravelMood.Sad
                4 -> Utils.TravelMood.Excited
                5 -> Utils.TravelMood.Angry
                else -> Utils.TravelMood.Happy
            }
        }
        slider.setLabelFormatter {
            when (it.toInt()) {
                1 -> "Happy"
                2 -> "Medium"
                3 -> "Sad"
                4 -> "Excited"
                5 -> "Angry"
                else -> "Happy"
            }
        }

        val buttonEdit = findViewById<Button>(R.id.button_edit)
        val button = findViewById<Button>(R.id.button_save)
        buttonEdit.setOnClickListener {
            title.isEnabled = true
            location.isEnabled = true
            startDate.isEnabled = true
            endDate.isEnabled = true
            notes.isEnabled = true
            spinner.isEnabled = true
            slider.isEnabled = true
            startDatePicker.isEnabled = true
            endDatePicker.isEnabled = true
            button.isEnabled = true
        }

        val intent : Intent = getIntent()
        val event = intent.getSerializableExtra("event") as Event?
        if(event != null){
            title.isEnabled = false
            location.isEnabled = false
            startDate.isEnabled = false
            endDate.isEnabled = false
            notes.isEnabled = false
            spinner.isEnabled = false
            slider.isEnabled = false
            startDatePicker.isEnabled = false
            endDatePicker.isEnabled = false
            button.isEnabled = false

            title.setText(event.title)
            location.setText(event.location)
            startDate.text = event.startDateTime
            endDate.text = event.endDateTime
            notes.setText(event.notes)
            spinner.setSelection(event.type.ordinal)
            slider.value = event.mood.ordinal.toFloat()
        }

        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(title.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else if (TextUtils.isEmpty(startDate.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else if (TextUtils.isEmpty(endDate.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else if (TextUtils.isEmpty(location.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else if (TextUtils.isEmpty(notes.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val duration = getDuration(startDate.text.toString(), endDate.text.toString())
                if (event != null) {
                    event.title = title.text.toString()
                    event.startDateTime = startDate.text.toString()
                    event.endDateTime = endDate.text.toString()
                    event.location = location.text.toString()
                    event.mood = mood
                    event.duration = duration
                    event.type = spinner.selectedItem as Utils.TravelType
                    event.notes = notes.text.toString()
                    replyIntent.putExtra(EXTRA_REPLY, event)
                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                }
                val newEvent = Event(
                    title = title.text.toString(),
                    startDateTime = startDate.text.toString(),
                    endDateTime = endDate.text.toString(),
                    location = location.text.toString(),
                    mood = mood,
                    duration = duration,
                    type = spinner.selectedItem as Utils.TravelType,
                    favorite = false,
                    notes = notes.text.toString()
                )
                replyIntent.putExtra(EXTRA_REPLY, newEvent)
                setResult(Activity.RESULT_OK, replyIntent)
            }

            finish()
        }
    }

    private fun getDuration(startDate: String, endDate: String): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        val start = LocalDate.parse(startDate, formatter)
        val end = LocalDate.parse(endDate, formatter)
        val days = ChronoUnit.DAYS.between(start, end)
        return "$days days"
    }

    private fun showDatePicker(textView: TextView) {
        val datePicker = DatePickerDialog(
            this,
            { DatePicker: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formatedDate = dateFormat.format(selectedDate.time)
                textView.text = formatedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }

}