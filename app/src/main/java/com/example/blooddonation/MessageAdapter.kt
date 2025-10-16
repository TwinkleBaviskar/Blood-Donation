package com.example.blooddonation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonation.R
import com.example.blooddonation.model.MessageModel

class MessageAdapter(
    private val messageList: List<MessageModel>,
    private val currentUserId: String
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSender: TextView = view.findViewById(R.id.tvSenderMessage)
        val tvReceiver: TextView = view.findViewById(R.id.tvReceiverMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]

        if (message.senderId == currentUserId) {
            holder.tvSender.visibility = View.VISIBLE
            holder.tvReceiver.visibility = View.GONE
            holder.tvSender.text = message.text
        } else {
            holder.tvSender.visibility = View.GONE
            holder.tvReceiver.visibility = View.VISIBLE
            holder.tvReceiver.text = message.text
        }
    }

    override fun getItemCount(): Int = messageList.size
}
