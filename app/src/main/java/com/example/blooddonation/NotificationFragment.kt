package com.example.blooddonation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blooddonation.adapter.NotificationAdapter
import com.example.blooddonation.databinding.FragmentNotificationBinding
import com.example.blooddonation.model.NotificationModel
import com.google.firebase.database.*

class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding

    private lateinit var db: FirebaseDatabase
    private lateinit var notiRef: DatabaseReference

    private val notificationList = ArrayList<NotificationModel>()
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseDatabase.getInstance(" YOUR DATABASE API"
        )

        notiRef = db.getReference("notifications").child("all")
        binding.recyclerNotifications.layoutManager = LinearLayoutManager(requireContext())
        adapter = NotificationAdapter(notificationList)
        binding.recyclerNotifications.adapter = adapter

        loadNotifications()
    }

    private fun loadNotifications() {
        notiRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()

                for (snap in snapshot.children) {
                    val model = snap.getValue(NotificationModel::class.java)
                    if (model != null) notificationList.add(model)
                }

                notificationList.sortByDescending { it.timestamp }
                adapter.notifyDataSetChanged()

                binding.txtEmpty.visibility =
                    if (notificationList.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
