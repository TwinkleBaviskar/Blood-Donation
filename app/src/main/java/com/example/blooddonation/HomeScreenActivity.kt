package com.example.blooddonation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.blooddonation.fragment.BloodBankFragment
import com.example.blooddonation.fragment.DonateFragment
import com.example.blooddonation.fragment.MessageFragment
import com.example.blooddonation.fragment.MessageListFragment

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var imgHome: ImageView
    private lateinit var imgMessage: ImageView
    private lateinit var imgFindDonor: ImageView
    private lateinit var imgDonate: ImageView
    private lateinit var imgBloodBank: ImageView
    private lateinit var imgReport: ImageView
    private lateinit var imgRequest: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)

        // 🔹 Initialize IDs
        imgHome = findViewById(R.id.imgHome)
        imgMessage = findViewById(R.id.imgMessage)
        imgFindDonor = findViewById(R.id.imgFindDonor)
        imgDonate = findViewById(R.id.imgDonate)
        imgBloodBank = findViewById(R.id.imgBloodBank)
        imgReport = findViewById(R.id.imgReport)
        imgRequest = findViewById(R.id.imgRequest)

        // 🏠 Home
        imgHome.setOnClickListener {
            findViewById<View>(R.id.scroll_content).visibility = View.VISIBLE
            findViewById<View>(R.id.fragment_container).visibility = View.GONE
        }

        // 💬 Message icon → open message list screen
        imgMessage.setOnClickListener {
            openFragment(MessageListFragment())
        }

        // 🔍 Find Donor
        imgFindDonor.setOnClickListener {
            val intent = Intent(this, FindDonorScreen::class.java)
            startActivity(intent)
        }

        // ❤️ Donate
        imgDonate.setOnClickListener {
            openFragment(DonateFragment())
        }

        // 🩸 Blood Bank
        imgBloodBank.setOnClickListener {
            openFragment(BloodBankFragment())
        }

        // 📊 Report Detail → open ReportActivity
        imgReport.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }

        // 📩 Request Button → Open RequestActivity
        imgRequest.setOnClickListener {
            val intent = Intent(this, RequestActivity::class.java)
            startActivity(intent)
        }

    }

    private fun openFragment(fragment: Fragment) {
        try {
            findViewById<View>(R.id.scroll_content).visibility = View.GONE
            findViewById<View>(R.id.fragment_container).visibility = View.VISIBLE

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to open screen", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment != null) {
            supportFragmentManager.popBackStack()
            findViewById<View>(R.id.scroll_content).visibility = View.VISIBLE
            findViewById<View>(R.id.fragment_container).visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    fun openMessageFragment(donorName: String) {
        try {
            runOnUiThread {
                findViewById<View>(R.id.scroll_content).visibility = View.GONE
                findViewById<View>(R.id.fragment_container).visibility = View.VISIBLE

                val fragment = MessageFragment.newInstance(donorName, "")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss()

                Toast.makeText(this, "Chat with $donorName", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Message feature not available yet", Toast.LENGTH_SHORT).show()
        }
    }
}
