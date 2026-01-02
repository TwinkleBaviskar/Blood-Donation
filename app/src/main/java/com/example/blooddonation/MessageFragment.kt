package com.example.blooddonation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonation.R
import com.example.blooddonation.adapter.MessageAdapter
import com.example.blooddonation.model.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MessageFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private lateinit var edtMessage: EditText
    private lateinit var btnSend: ImageView
    private lateinit var txtHeader: TextView
    private val messageList = ArrayList<MessageModel>()
    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var msgRef: DatabaseReference
    private lateinit var convRef: DatabaseReference

    private var otherUserId = ""
    private var otherName = ""

    private val DATABASE_URL =
        "YOUR DATABASE API"

    private val currentUserId get() = auth.currentUser?.uid ?: ""

    companion object {
        fun newInstance(otherId: String, otherName: String): MessageFragment {
            val f = MessageFragment()
            val b = Bundle()
            b.putString("otherUserId", otherId)
            b.putString("otherName", otherName)
            f.arguments = b
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance(DATABASE_URL)

        otherUserId = arguments?.getString("otherUserId") ?: ""
        otherName = arguments?.getString("otherName") ?: ""

        val chatId = if (currentUserId < otherUserId)
            "${currentUserId}_${otherUserId}"
        else
            "${otherUserId}_${currentUserId}"

        msgRef = db.getReference("messages").child(chatId)
        convRef = db.getReference("conversations")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_message, container, false)

        recyclerView = v.findViewById(R.id.recyclerMessages)
        edtMessage = v.findViewById(R.id.edtMessage)
        btnSend = v.findViewById(R.id.btnSend)
        txtHeader = v.findViewById(R.id.txtHeaderName)
        txtHeader.text = otherName

        recyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }

        adapter = MessageAdapter(messageList, currentUserId)
        recyclerView.adapter = adapter

        btnSend.setOnClickListener { sendMessage() }

        listenMessages()

        return v
    }

    private fun sendMessage() {
        val text = edtMessage.text.toString().trim()
        if (text.isEmpty()) return

        val key = msgRef.push().key ?: return

        val msg = MessageModel(
            senderId = currentUserId,
            receiverId = otherUserId,
            text = text,
            timestamp = System.currentTimeMillis()
        )

        msgRef.child(key).setValue(msg).addOnSuccessListener {
            updateConversations(text)
        }

        edtMessage.setText("")
    }

    private fun updateConversations(text: String) {

        val time = System.currentTimeMillis()
        val senderMap = mapOf(
            "otherUserId" to otherUserId,
            "otherName" to otherName,
            "lastMessage" to text,
            "lastTimestamp" to time
        )
        convRef.child(currentUserId).child(otherUserId).setValue(senderMap)
        val myName = auth.currentUser?.email?.substringBefore("@") ?: "User"
        val receiverMap = mapOf(
            "otherUserId" to currentUserId,
            "otherName" to myName,
            "lastMessage" to text,
            "lastTimestamp" to time
        )
        convRef.child(otherUserId).child(currentUserId).setValue(receiverMap)
    }

    private fun listenMessages() {
        msgRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (m in snapshot.children) {
                    val message = m.getValue(MessageModel::class.java)
                    if (message != null) messageList.add(message)
                }
                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(messageList.size - 1)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
