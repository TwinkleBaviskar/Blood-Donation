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

class MessageFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private lateinit var edtMessage: EditText   // ✅ Changed here
    private lateinit var btnSend: ImageView
    private lateinit var txtHeaderName: TextView

    private val messageList = mutableListOf<MessageModel>()

    private var currentUserId = "user1"
    private var receiverId: String = "user2"
    private var donorName: String? = null

    companion object {
        fun newInstance(donorName: String, donorId: String): MessageFragment {
            val fragment = MessageFragment()
            val args = Bundle()
            args.putString("donorName", donorName)
            args.putString("donorId", donorId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        donorName = arguments?.getString("donorName")
        receiverId = arguments?.getString("donorId") ?: "user2"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_message, container, false)

        recyclerView = view.findViewById(R.id.recyclerMessages)
        edtMessage = view.findViewById(R.id.edtMessage)   // ✅ Matches <EditText> in XML
        btnSend = view.findViewById(R.id.btnSend)
        txtHeaderName = view.findViewById(R.id.txtHeaderName)

        txtHeaderName.text = donorName ?: "Chat"

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MessageAdapter(messageList, currentUserId)
        recyclerView.adapter = adapter

        btnSend.setOnClickListener {
            val msgText = edtMessage.text.toString().trim()
            if (msgText.isNotEmpty()) {
                val message = MessageModel(currentUserId, receiverId, msgText)
                messageList.add(message)
                adapter.notifyItemInserted(messageList.size - 1)
                recyclerView.scrollToPosition(messageList.size - 1)
                edtMessage.setText("")
            }
        }

        return view
    }
}
