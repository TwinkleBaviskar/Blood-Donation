package com.example.blooddonation.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blooddonation.R
import com.example.blooddonation.adapter.BloodDonorAdapter
import com.example.blooddonation.model.BloodDonorModel
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DonateFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BloodDonorAdapter
    private val donorList = ArrayList<BloodDonorModel>()

    private lateinit var database: FirebaseDatabase
    private lateinit var requestRef: DatabaseReference

    // Handler for periodic timeAgo updates
    private val timeHandler = Handler(Looper.getMainLooper())
    private val timeUpdateIntervalMs = 60_000L // update every 60 seconds

    private val timeUpdater = object : Runnable {
        override fun run() {
            try {
                // Recompute timeAgo for each model and refresh adapter
                val refreshed = donorList.map { model ->
                    val newTimeAgo = if (model.timestamp > 0L) {
                        formatTimeAgo(model.timestamp)
                    } else {
                        "time unknown"
                    }
                    model.copy(timeAgo = newTimeAgo)
                }

                donorList.clear()
                donorList.addAll(refreshed)

                // Keep default sorting (urgent first then latest)
                val sorted = donorList.sortedWith(
                    compareByDescending<BloodDonorModel> { it.isUrgent }
                        .thenByDescending { it.timestamp }
                )
                adapter.updateList(sorted)
            } catch (e: Exception) {
                Log.e("DonateFragment", "Error in timeUpdater: ${e.message}")
            } finally {
                timeHandler.postDelayed(this, timeUpdateIntervalMs)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_donate, container, false)

        // Back button
        val backArrow: ImageView = view.findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        recyclerView = view.findViewById(R.id.recyclerDonors)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        database = FirebaseDatabase.getInstance("https://blooddonation-bbec8-default-rtdb.asia-southeast1.firebasedatabase.app/")
        requestRef = database.getReference("requests")

        adapter = BloodDonorAdapter(requireContext(), listOf()) { requestId ->
            markRequestAsCompleted(requestId)
        }

        recyclerView.adapter = adapter

        // Load requests
        loadRequestsFromFirebase()

        val btnAll = view.findViewById<Button>(R.id.btnAll)
        val btnRecent = view.findViewById<Button>(R.id.btnRecent)
        val btnUrgent = view.findViewById<Button>(R.id.btnUrgent)

        // Default selected visual
        btnAll.isSelected = true
        btnRecent.isSelected = false
        btnUrgent.isSelected = false

        // ALL: urgent first, then normal, within each group newest first
        btnAll.setOnClickListener {
            btnAll.isSelected = true
            btnRecent.isSelected = false
            btnUrgent.isSelected = false

            val allList = donorList.sortedWith(
                compareByDescending<BloodDonorModel> { it.isUrgent }
                    .thenByDescending { it.timestamp }
            )
            adapter.updateList(allList)
        }

        // RECENT: requests within last 8 hours, newest first
        btnRecent.setOnClickListener {
            btnAll.isSelected = false
            btnRecent.isSelected = true
            btnUrgent.isSelected = false

            val currentTime = System.currentTimeMillis()
            val recentList = donorList.filter {
                it.timestamp > 0 && (currentTime - it.timestamp) <= TimeUnit.HOURS.toMillis(8)
            }.sortedByDescending { it.timestamp }
            adapter.updateList(recentList)
        }

        // URGENT: only urgent requests, newest first
        btnUrgent.setOnClickListener {
            btnAll.isSelected = false
            btnRecent.isSelected = false
            btnUrgent.isSelected = true

            val urgentList = donorList.filter { it.isUrgent }
                .sortedByDescending { it.timestamp }
            adapter.updateList(urgentList)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Start periodic updates for timeAgo labels
        timeHandler.post(timeUpdater)
    }

    override fun onPause() {
        super.onPause()
        // Stop updates to avoid leaks
        timeHandler.removeCallbacks(timeUpdater)
    }

    private fun loadRequestsFromFirebase() {
        requestRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val parsedList = withContext(Dispatchers.Default) {
                        val temp = ArrayList<BloodDonorModel>()

                        if (snapshot.exists()) {
                            for (reqSnap in snapshot.children) {
                                try {
                                    val requestId = reqSnap.key ?: continue
                                    val name = reqSnap.child("patientName").getValue(String::class.java) ?: "Unknown"
                                    val bloodGroup = reqSnap.child("bloodGroup").getValue(String::class.java) ?: "-"
                                    val urgency = reqSnap.child("urgency").getValue(String::class.java) ?: "Normal"
                                    val hospital = reqSnap.child("hospital").getValue(String::class.java) ?: "Not specified"
                                    val contact = reqSnap.child("contact").getValue(String::class.java) ?: "N/A"
                                    val status = reqSnap.child("status").getValue(String::class.java) ?: "active"

                                    // Safe timestamp parsing via normalizeTimestamp()
                                    val tsValue = reqSnap.child("timestamp").value
                                    val timeMillis: Long = normalizeTimestamp(tsValue)

                                    // Debug (important small line to help if still fails)
                                    Log.d("DonateFragment", "Request: $requestId | Raw: $tsValue | Parsed: $timeMillis | TimeAgo: ${formatTimeAgo(timeMillis)}")

                                    val isUrgent = urgency.equals("Emergency", ignoreCase = true) ||
                                            urgency.equals("Urgent", ignoreCase = true)

                                    val timeAgoText = if (timeMillis <= 0L) "time unknown" else formatTimeAgo(timeMillis)

                                    temp.add(
                                        BloodDonorModel(
                                            name = name,
                                            bloodGroup = bloodGroup,
                                            location = hospital,
                                            timeAgo = timeAgoText,
                                            isUrgent = isUrgent,
                                            timestamp = timeMillis,
                                            requestStatus = status,
                                            phone = contact,
                                            requestId = requestId
                                        )
                                    )
                                } catch (e: Exception) {
                                    Log.e("DonateFragment", "Error parsing request: ${e.message}", e)
                                }
                            }
                        }
                        temp
                    }

                    // Update UI on main thread
                    donorList.clear()
                    donorList.addAll(parsedList)

                    // Always update adapter (never leave it stale)
                    val sorted = donorList.sortedWith(
                        compareByDescending<BloodDonorModel> { it.isUrgent }
                            .thenByDescending { it.timestamp }
                    )
                    adapter.updateList(sorted)

                    if (donorList.isEmpty()) {
                        Toast.makeText(requireContext(), "No active requests!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun markRequestAsCompleted(requestId: String) {
        requestRef.child(requestId).child("status").setValue("completed")
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "✅ Request marked as completed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "❌ Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Robust timestamp normalization
    private fun normalizeTimestamp(raw: Any?): Long {
        try {
            // Handle numeric types directly
            when (raw) {
                is Long -> return if (raw < 1_000_000_000_000L) raw * 1000L else raw
                is Int -> {
                    val v = raw.toLong()
                    return if (v < 1_000_000_000_000L) v * 1000L else v
                }
                is Double -> {
                    val v = raw.toLong()
                    return if (v < 1_000_000_000_000L) v * 1000L else v
                }
                is Float -> {
                    val v = raw.toLong()
                    return if (v < 1_000_000_000_000L) v * 1000L else v
                }
                is String -> {
                    val s = raw.trim()
                    // numeric string?
                    s.toLongOrNull()?.let { n ->
                        return if (n < 1_000_000_000_000L) n * 1000L else n
                    }
                    // try common date formats like "yyyy-MM-dd HH:mm:ss"
                    val patterns = arrayOf(
                        "yyyy-MM-dd HH:mm:ss",
                        "yyyy-MM-dd'T'HH:mm:ss'Z'",
                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                        "yyyy-MM-dd'T'HH:mm:ss",
                        "yyyy-MM-dd HH:mm"
                    )
                    for (p in patterns) {
                        try {
                            val sdf = SimpleDateFormat(p, Locale.getDefault())
                            if (p.contains("'Z'")) sdf.timeZone = TimeZone.getTimeZone("UTC")
                            val d = sdf.parse(s)
                            if (d != null) return d.time
                        } catch (_: Exception) { /* try next */ }
                    }
                    // last attempt: ISO style
                    try {
                        val iso = s.replace(" ", "T")
                        val instant = java.time.Instant.parse("${iso}Z")
                        return instant.toEpochMilli()
                    } catch (_: Exception) { /* ignore */ }
                    Log.w("DonateFragment", "Invalid string timestamp: $s")
                    return System.currentTimeMillis()
                }
                // Firebase ServerValue may appear as a Map while pending; handle that
                is Map<*, *> -> {
                    // Common case: {\".sv\"=\"timestamp\"} or similar - treat as pending timestamp
                    if (raw.containsKey(".sv") || raw.containsKey("\$sv") || raw.containsKey("timestamp")) {
                        // Use current time as temporary value until server resolves
                        return System.currentTimeMillis()
                    }
                    // else unknown map: fallback
                    return System.currentTimeMillis()
                }
                is com.google.firebase.Timestamp -> return raw.toDate().time
                null -> return System.currentTimeMillis()
                else -> return System.currentTimeMillis()
            }
        } catch (e: Exception) {
            Log.e("DonateFragment", "normalizeTimestamp error: ${e.message}", e)
            return System.currentTimeMillis()
        }
    }

    /**
     * Format timestamp to human-readable "time ago" string
     */
    private fun formatTimeAgo(timestamp: Long): String {
        if (timestamp <= 0) return "time unknown"

        val now = System.currentTimeMillis()
        val diffMillis = now - timestamp

        // Handle future timestamps
        if (diffMillis < 0) {
            return "just now"
        }

        val seconds = TimeUnit.MILLISECONDS.toSeconds(diffMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
        val days = TimeUnit.MILLISECONDS.toDays(diffMillis)

        return when {
            seconds < 60 -> "just now"
            minutes < 60 -> "$minutes min ago"
            hours < 24 -> "$hours hour${if (hours > 1) "s" else ""} ago"
            days < 7 -> "$days day${if (days > 1) "s" else ""} ago"
            days < 30 -> "${days / 7} week${if (days / 7 > 1) "s" else ""} ago"
            else -> "${days / 30} month${if (days / 30 > 1) "s" else ""} ago"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timeHandler.removeCallbacks(timeUpdater)
    }
}
