package com.example.trip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.databinding.RecyclerviewItemBinding

class EventListAdapter (
    private val listener: (Event) -> Unit,
    private val favoriteListener: (Event) -> Unit
) : ListAdapter<Event, EventListAdapter.EventViewHolder>(EventComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, favoriteListener)
        holder.itemView.setOnClickListener {
            listener(current)
        }
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = RecyclerviewItemBinding.bind(itemView)

        fun bind(event: Event?, favoriteListener: (Event) -> Unit) {
            binding.eventTitle.text = "Trip to ${event?.title}"
            binding.eventDate.text = event?.startDateTime + " - " + event?.endDateTime
            binding.eventLocation.text = event?.location
            event?.favorite?.let { binding.eventSwitch.isChecked = it }
            binding.eventSwitch.setOnCheckedChangeListener { _, isChecked ->
                event?.let { favoriteListener(it) }
            }
        }

        companion object {
            fun create(parent: ViewGroup): EventViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return EventViewHolder(view)
            }
        }
    }

    class EventComparator : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem._id == newItem._id
        }
    }
}