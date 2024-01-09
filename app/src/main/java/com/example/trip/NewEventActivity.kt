package com.example.trip

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.slider.Slider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewEventActivity : AppCompatActivity() {

    private lateinit var editEventView: EditText
    lateinit var startDate: TextView
    lateinit var endDate: TextView
    lateinit var startDatePicker: Button
    lateinit var endDatePicker: Button
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_event)
        editEventView = findViewById(R.id.edit_title)

        startDate = findViewById(R.id.startDateText)
        endDate = findViewById(R.id.endDateText)
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

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editEventView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val word = editEventView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, word)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
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