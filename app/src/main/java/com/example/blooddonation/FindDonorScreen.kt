package com.example.blooddonation

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class FindDonorScreen : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DonorAdapter
    private val donorList = ArrayList<DonorModel>()

    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_donor_screen)

        // 🔙 Back button
        val backArrow: ImageView = findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            startActivity(Intent(this, HomeScreenActivity::class.java))
            finish()
        }

        recyclerView = findViewById(R.id.recyclerDonors)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        database = FirebaseDatabase.getInstance("https://blooddonation-bbec8-default-rtdb.asia-southeast1.firebasedatabase.app/")
        userRef = database.getReference("users")

        adapter = DonorAdapter(this, donorList)
        recyclerView.adapter = adapter

        loadDonorsFromFirebase()
    }

    private fun loadDonorsFromFirebase() {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                donorList.clear()
                if (snapshot.exists()) {
                    for (userSnap in snapshot.children) {
                        val donor = userSnap.getValue(DonorModel::class.java)
                        donor?.let { donorList.add(it) }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@FindDonorScreen, "No donors found!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FindDonorScreen, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
