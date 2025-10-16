package com.example.blooddonation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonation.HomeScreenActivity
import com.example.blooddonation.R
import com.example.blooddonation.adapter.MessageListAdapter
import com.example.blooddonation.model.MessageListModel

class MessageListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageListAdapter
    private lateinit var chatList: ArrayList<MessageListModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_message_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerChatList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        chatList = arrayListOf(
            MessageListModel("Priya Sharma", "Hey, blood mil gaya kya?", "2m ago"),
            MessageListModel("Rahul Mehta", "Thank you so much!", "10m ago"),
            MessageListModel("John Doe", "Hello, I am ready to donate.", "1h ago")
        )

        adapter = MessageListAdapter(chatList) { name ->
            (activity as? HomeScreenActivity)?.openMessageFragment(name)
        }
        recyclerView.adapter = adapter

        return view
    }
}
