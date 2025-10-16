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
            finish()
        }

        recyclerView = findViewById(R.id.recyclerDonors)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        donorList = ArrayList()

        // ✅ Updated Dummy Data with all details
        donorList.add(
            DonorModel(
                name = "Liam Elijah",
                location = "New York",
                bloodGroup = "A+",
                imageRes = R.drawable.profile,
                lastDonation = "2 months ago",
                livesSaved = 12,
                totalDonations = 5
            )
        )
        donorList.add(
            DonorModel(
                name = "Oliver James",
                location = "Washington, D.C.",
                bloodGroup = "B-",
                imageRes = R.drawable.profile,
                lastDonation = "5 months ago",
                livesSaved = 8,
                totalDonations = 3
            )
        )
        donorList.add(
            DonorModel(
                name = "Michel Lucas",
                location = "Arizona",
                bloodGroup = "O+",
                imageRes = R.drawable.profile,
                lastDonation = "1 year ago",
                livesSaved = 15,
                totalDonations = 7
            )
        )
        donorList.add(
            DonorModel(
                name = "Benjamin Jack",
                location = "Florida",
                bloodGroup = "AB-",
                imageRes = R.drawable.profile,
                lastDonation = "3 weeks ago",
                livesSaved = 10,
                totalDonations = 4
            )
        )
        donorList.add(
            DonorModel(
                name = "Archer Noah",
                location = "Georgia",
                bloodGroup = "A-",
                imageRes = R.drawable.profile,
                lastDonation = "6 months ago",
                livesSaved = 7,
                totalDonations = 2
            )
        )
        donorList.add(
            DonorModel(
                name = "Daniel Henry",
                location = "California",
                bloodGroup = "B+",
                imageRes = R.drawable.profile,
                lastDonation = "1 month ago",
                livesSaved = 9,
                totalDonations = 6
            )
        )

        // ✅ Adapter with context passed
        adapter = DonorAdapter(this, donorList)
        recyclerView.adapter = adapter
    }
}
