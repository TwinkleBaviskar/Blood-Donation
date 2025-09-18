package com.example.blooddonation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blooddonation.databinding.ActivityForgotpassBinding

class ForgotpassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotpassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityForgotpassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Go back to LoginActivity when "Sign In" is clicked
        binding.txtSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Send Code button -> open OTP screen
        binding.root.findViewById<Button>(R.id.sendCodeButton)?.setOnClickListener {
            val intent = Intent(this, OtpScreenActivity::class.java)
            startActivity(intent)
        }
    }
}
