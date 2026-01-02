package com.example.blooddonation.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonation.R
import com.example.blooddonation.model.MessageListModel
import java.text.SimpleDateFormat
import java.util.*

class MessageListAdapter(
    private var list: List<MessageListModel>,
    private val onClick: (MessageListModel) -> Unit
) : RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtUserName)
        val txtLast: TextView = view.findViewById(R.id.txtLastMsg)
        val txtTime: TextView = view.findViewById(R.id.txtTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_user, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val user = list[pos]
        holder.txtName.text = user.name

        if (user.lastMessage.isNotEmpty()) {

            holder.txtLast.visibility = View.VISIBLE
            holder.txtTime.visibility = View.VISIBLE

            holder.txtLast.text = user.lastMessage
            holder.txtName.setTypeface(null, Typeface.BOLD)

            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            holder.txtTime.text = sdf.format(Date(user.lastTimestamp))

        } else {

            holder.txtLast.visibility = View.GONE
            holder.txtTime.visibility = View.GONE
            holder.txtName.setTypeface(null, Typeface.NORMAL)
        }
        holder.itemView.setOnClickListener { onClick(user) }
    }

    fun updateList(newList: List<MessageListModel>) {
        list = newList
        notifyDataSetChanged()
    }
}
