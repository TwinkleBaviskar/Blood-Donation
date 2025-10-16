package com.example.blooddonation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonation.R
import com.example.blooddonation.model.MessageListModel

class MessageListAdapter(
    private val chatList: List<MessageListModel>,
    private val onChatClick: (String) -> Unit
) : RecyclerView.Adapter<MessageListAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Using IDs from item_chat_user.xml
        val txtName: TextView = itemView.findViewById(R.id.txtUserName)
        val txtMessage: TextView = itemView.findViewById(R.id.txtLastMsg)
        val txtTime: TextView = itemView.findViewById(R.id.txtTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // Inflate the layout file you actually created (item_chat_user.xml)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_user, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.txtName.text = chat.name
        holder.txtMessage.text = chat.lastMessage
        holder.txtTime.text = chat.time

        holder.itemView.setOnClickListener {
            onChatClick(chat.name)
        }
    }

    override fun getItemCount(): Int = chatList.size
}
