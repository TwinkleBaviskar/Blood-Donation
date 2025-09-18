package com.example.blooddonation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blooddonation.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sign Up Button Click
        binding.btnsignup.setOnClickListener {
            if (binding.checked.isChecked) {
                // Navigate to Home Screen
                val intent = Intent(this, HomeScreenActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Show message if checkbox not ticked
                Toast.makeText(
                    this,
                    "Please agree to the terms and conditions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Already have an account? → Go to Login Screen
        binding.txtSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
