package com.example.blooddonation

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FindDonorScreen : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DonorAdapter
    private lateinit var donorList: ArrayList<DonorModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_donor_screen)

        // 🔙 Back Arrow click listener
        val backArrow: ImageView = findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            val intent = Intent(this, HomeScreenActivity::class.java) // Replace with your home screen
            startActivity(intent)
            finish() // Optional: close current activity
        }

        recyclerView = findViewById(R.id.recyclerDonors)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        donorList = ArrayList()

        // Dummy Data
        donorList.add(DonorModel("Liam Elijah", "New York", "A+", R.drawable.profile))
        donorList.add(DonorModel("Oliver James", "Washington, D.C.", "B-", R.drawable.profile))
        donorList.add(DonorModel("Michel Lucas", "Arizona", "O+", R.drawable.profile))
        donorList.add(DonorModel("Benjamin Jack", "Florida", "AB-", R.drawable.profile))
        donorList.add(DonorModel("Archer Noah", "Georgia", "A-", R.drawable.profile))
        donorList.add(DonorModel("Daniel Henry", "California", "B+", R.drawable.profile))
        donorList.add(DonorModel("Benjamin Jack", "Florida", "AB-", R.drawable.profile))
        donorList.add(DonorModel("Archer Noah", "Georgia", "A-", R.drawable.profile))
        donorList.add(DonorModel("Daniel Henry", "California", "B+", R.drawable.profile))

        adapter = DonorAdapter(donorList)
        recyclerView.adapter = adapter
    }
}
