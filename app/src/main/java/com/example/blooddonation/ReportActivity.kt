package com.example.blooddonation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import com.example.blooddonation.adapter.ReportAdapter
import com.example.blooddonation.model.ReportModel

class ReportActivity : AppCompatActivity() {

    private lateinit var adapter: ReportAdapter
    private lateinit var reportList: ArrayList<ReportModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val etSearch = findViewById<EditText>(R.id.etSearch)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerReports)

        // 🧠 Dummy data – later you can replace this with Firebase or Registration data
        reportList = arrayListOf(
            ReportModel("Aarav Mehta", "A+", "65 kg", "01 Aug 2024", "01 Nov 2024", "14.2 g/dL", "Eligible"),
            ReportModel("Riya Sharma", "B-", "50 kg", "10 Sep 2024", "10 Dec 2024", "11.8 g/dL", "Not Eligible"),
            ReportModel("Kabir Singh", "O+", "72 kg", "20 Jul 2024", "20 Oct 2024", "15.1 g/dL", "Eligible"),
            ReportModel("Meena Patel", "AB+", "54 kg", "15 Aug 2024", "15 Nov 2024", "12.5 g/dL", "Eligible")
        )

        // ✅ Adapter setup
        adapter = ReportAdapter(reportList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 🔍 Live Search functionality
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
        })
    }

    // 🔎 Search filter logic
    private fun filter(text: String) {
        val filteredList = ArrayList<ReportModel>()
        for (item in reportList) {
            if (item.name.contains(text, ignoreCase = true)) {
                filteredList.add(item)
            }
        }
        adapter.filterList(filteredList)
    }
}
