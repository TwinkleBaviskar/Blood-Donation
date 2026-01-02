package com.example.blooddonation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)

        Handler(Looper.getMainLooper()).postDelayed({
            checkUserStatus()
        }, 2000)
    }

    private fun checkUserStatus() {
        val currentUser = auth.currentUser
        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)

        when {
            currentUser != null -> {
                startActivity(Intent(this, HomeScreenActivity::class.java))
            }
            isFirstLaunch -> {
                prefs.edit().putBoolean("isFirstLaunch", false).apply()
                startActivity(Intent(this, IntroActivity1::class.java))
            }
            else -> {
                startActivity(Intent(this, AuthActivity::class.java))
            }
        }
        finish()
    }
}
