package com.example.blooddonation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonation.R
import com.example.blooddonation.model.MessageModel
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val list: List<MessageModel>,
    private val currentUid: String
) : RecyclerView.Adapter<MessageAdapter.Holder>() {

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSender: TextView = view.findViewById(R.id.tvSenderMessage)
        val tvReceiver: TextView = view.findViewById(R.id.tvReceiverMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return Holder(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(h: Holder, pos: Int) {
        val m = list[pos]

        if (m.senderId == currentUid) {
            // Sender
            h.tvSender.visibility = View.VISIBLE
            h.tvReceiver.visibility = View.GONE
            h.tvSender.text = m.text

        } else {
            // Receiver
            h.tvSender.visibility = View.GONE
            h.tvReceiver.visibility = View.VISIBLE
            h.tvReceiver.text = m.text
        }
    }
}
