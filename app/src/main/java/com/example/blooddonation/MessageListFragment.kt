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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class MessageListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageListAdapter
    private val userList = ArrayList<MessageListModel>()

    private lateinit var db: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var conversationsRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private val DATABASE_URL =
        "YOUR DATABSE API"

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "anon"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_message_list, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance(DATABASE_URL)

        usersRef = db.getReference("users")
        conversationsRef = db.getReference("conversations").child(currentUserId)

        recyclerView = view.findViewById(R.id.recyclerChatList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = MessageListAdapter(userList) { clickedUser ->
            val act = activity as? HomeScreenActivity ?: return@MessageListAdapter
            act.openMessageFragment(clickedUser.userId, clickedUser.name)
        }

        recyclerView.adapter = adapter

        loadUsersWithChats()
        return view
    }

    private fun loadUsersWithChats() {

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(usersSnap: DataSnapshot) {

                userList.clear()

                for (u in usersSnap.children) {

                    val uid = u.key ?: continue
                    if (uid == currentUserId) continue

                    val fullName = u.child("fullName").value.toString()

                    userList.add(
                        MessageListModel(
                            userId = uid,
                            name = fullName,
                            lastMessage = "",
                            lastTimestamp = 0L,
                            unreadCount = 0
                        )
                    )
                }

                // STEP 2 — Load Chat History and Merge
                conversationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(convSnap: DataSnapshot) {

                        for (user in userList) {
                            val conv = convSnap.child(user.userId)

                            if (conv.exists()) {
                                user.lastMessage =
                                    conv.child("lastMessage").getValue(String::class.java) ?: ""

                                user.lastTimestamp =
                                    conv.child("lastTimestamp").getValue(Long::class.java) ?: 0L
                            }
                        }

                        // STEP 3 — SORTING (chat users UP, normal DOWN)
                        userList.sortWith(
                            compareByDescending<MessageListModel> { it.lastTimestamp > 0 }
                                .thenByDescending { it.lastTimestamp }
                                .thenBy { it.name.lowercase(Locale.getDefault()) }
                        )

                        adapter.updateList(userList)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
