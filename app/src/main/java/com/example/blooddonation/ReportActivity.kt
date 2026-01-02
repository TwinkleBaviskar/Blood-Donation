package com.example.blooddonation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonation.adapter.ReportAdapter
import com.example.blooddonation.model.ReportModel
import com.google.firebase.database.*

class ReportActivity : AppCompatActivity() {

    private lateinit var adapter: ReportAdapter
    private var reportList: ArrayList<ReportModel> = ArrayList()
    private var allReports: ArrayList<ReportModel> = ArrayList()

    private lateinit var dbRef: DatabaseReference
    private val DATABASE_URL ="YOUR DATABASE API"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val etSearch = findViewById<EditText>(R.id.etSearch)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerReports)
        val db = FirebaseDatabase.getInstance(DATABASE_URL)


        dbRef = db.getReference("users")
        adapter = ReportAdapter(reportList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadReportsFromUsersTable()

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
        })
    }

    private fun loadReportsFromUsersTable() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reportList.clear()
                allReports.clear()

                if (!snapshot.hasChildren()) {
                    Toast.makeText(
                        this@ReportActivity,
                        "users table me koi data nahi mila",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                for (userSnapshot in snapshot.children) {

                    val name =
                        userSnapshot.child("fullName").getValue(String::class.java)
                            ?: userSnapshot.child("name").getValue(String::class.java)
                            ?: "Unknown"

                    val bloodGroup =
                        userSnapshot.child("bloodGroup").getValue(String::class.java)

                    val weightRaw =
                        userSnapshot.child("weight").getValue(String::class.java)
                    val weight = when {
                        weightRaw.isNullOrEmpty() -> null
                        weightRaw.contains("kg", ignoreCase = true) -> weightRaw
                        else -> "$weightRaw kg"
                    }

                    val lastDonation =
                        userSnapshot.child("lastDonation").getValue(String::class.java)

                    val hbRaw =
                        userSnapshot.child("hemoglobin").getValue(String::class.java)
                    val hemoglobin = when {
                        hbRaw.isNullOrEmpty() -> null
                        hbRaw.contains("g/dL", ignoreCase = true) -> hbRaw
                        else -> "$hbRaw g/dL"
                    }

                    val report = ReportModel(
                        name = name,
                        bloodGroup = bloodGroup,
                        weight = weight,
                        lastDonation = lastDonation,
                        hemoglobin = hemoglobin
                    )

                    reportList.add(report)
                    allReports.add(report)
                }

                adapter.filterList(ArrayList(reportList))
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ReportActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun filter(text: String) {
        val filteredList = ArrayList<ReportModel>()

        if (text.isEmpty()) {
            filteredList.addAll(allReports)
        } else {
            for (item in allReports) {
                val name = item.name ?: ""
                if (name.contains(text, ignoreCase = true)) {
                    filteredList.add(item)
                }
            }
        }

        adapter.filterList(filteredList)
    }
}
