package com.example.blooddonation

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blooddonation.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Login Button (simple local check)
        binding.btnSignIn.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.editTextEmail.error = "Enter valid email"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.editTextPassword.error = "Enter password"
                return@setOnClickListener
            }

            // 🔹 Fake local login success
            Toast.makeText(this, "Login successful 🎉", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeScreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 🔹 Go to Sign Up
        binding.txtSignup.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        // 🔹 Forgot Password
        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotpassActivity::class.java))
        }
    }
}
