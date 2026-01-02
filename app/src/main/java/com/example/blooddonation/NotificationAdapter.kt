package com.example.blooddonation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonation.R
import com.example.blooddonation.model.NotificationModel
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(
    private val list: List<NotificationModel>
) : RecyclerView.Adapter<NotificationAdapter.NotiViewHolder>() {

    class NotiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.imgIcon)
        val title: TextView = itemView.findViewById(R.id.textTitle)
        val message: TextView = itemView.findViewById(R.id.textMessage)
        val time: TextView = itemView.findViewById(R.id.textTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotiViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item.title
        holder.message.text = item.message
        holder.time.text = formatTime(item.timestamp)
    }

    override fun getItemCount(): Int = list.size

    private fun formatTime(ts: Long): String {
        if (ts == 0L) return ""
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(ts))
    }
}
