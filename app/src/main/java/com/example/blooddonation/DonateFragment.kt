package com.example.blooddonation.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonation.HomeScreenActivity
import com.example.blooddonation.R
import com.example.blooddonation.adapter.BloodDonorAdapter
import com.example.blooddonation.model.BloodDonorModel
import java.util.concurrent.TimeUnit

class DonateFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BloodDonorAdapter
    private lateinit var donorList: ArrayList<BloodDonorModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_donate, container, false)

        recyclerView = view.findViewById(R.id.recyclerDonors)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val currentTime = System.currentTimeMillis()

        donorList = arrayListOf(
            BloodDonorModel("John Doe", "A+", "Pune", "5 min ago", true, currentTime - TimeUnit.MINUTES.toMillis(5), "active", "9999999999"),
            BloodDonorModel("Priya Sharma", "B+", "Mumbai", "2 hr ago", false, currentTime - TimeUnit.HOURS.toMillis(2), "active", "8888888888"),
            BloodDonorModel("Rahul Mehta", "O-", "Delhi", "6 hr ago", false, currentTime - TimeUnit.HOURS.toMillis(6), "active", "7777777777"),
            BloodDonorModel("Sneha Patil", "AB+", "Nashik", "9 hr ago", false, currentTime - TimeUnit.HOURS.toMillis(9), "expired", "6666666666"),
            BloodDonorModel("Karan Singh", "B-", "Indore", "12 hr ago", true, currentTime - TimeUnit.HOURS.toMillis(12), "active", "5555555555")
        )

        adapter = BloodDonorAdapter(requireContext(), donorList)
        recyclerView.adapter = adapter

        val btnAll = view.findViewById<Button>(R.id.btnAll)
        val btnRecent = view.findViewById<Button>(R.id.btnRecent)
        val btnUrgent = view.findViewById<Button>(R.id.btnUrgent)

        btnAll.setOnClickListener {
            adapter.updateList(donorList)
        }

        btnRecent.setOnClickListener {
            val recentList = donorList.filter {
                val diff = currentTime - it.timestamp
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                hours <= 8 && it.requestStatus == "active"
            }
            adapter.updateList(recentList)
        }

        btnUrgent.setOnClickListener {
            val urgentList = donorList.filter { it.isUrgent && it.requestStatus == "active" }
            adapter.updateList(urgentList)
        }

        return view
    }

    fun showContactDialog(name: String, phone: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Contact $name")
        builder.setItems(arrayOf("❌ Cancel","💬 Message","📞 Call" )) { dialog, which ->
            when (which) {
                0 -> {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:$phone")
                    startActivity(intent)
                }
                1 -> {
                    Toast.makeText(requireContext(), "Opening chat with $name", Toast.LENGTH_SHORT).show()

                    // ✅ Safe Fragment → Activity call
                    requireActivity().runOnUiThread {
                        (requireActivity() as? HomeScreenActivity)?.openMessageFragment(name)
                    }
                }
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }
}
