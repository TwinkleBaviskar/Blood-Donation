package com.example.blooddonation

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
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

        binding.btnsignup.setOnClickListener {
            val fullName = binding.editFullName.text.toString().trim()
            val email = binding.editEmail.text.toString().trim()
            val password = binding.editPassword.text.toString().trim()
            val bloodGroup = binding.editBloodGroup.text.toString().trim()
            val country = binding.editCountry.text.toString().trim()

            if (validateInput(fullName, email, password, bloodGroup, country)) {
                Toast.makeText(this, "Registration successful ✅", Toast.LENGTH_SHORT).show()

                // Redirect to HomeScreen
                val intent = Intent(this, HomeScreenActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.txtSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInput(
        fullName: String,
        email: String,
        password: String,
        bloodGroup: String,
        country: String
    ): Boolean {
        if (fullName.isEmpty()) {
            binding.editFullName.error = "Enter full name"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editEmail.error = "Enter valid email"
            return false
        }
        if (password.length < 6) {
            binding.editPassword.error = "Password must be at least 6 chars"
            return false
        }
        if (bloodGroup.isEmpty()) {
            binding.editBloodGroup.error = "Enter blood group"
            return false
        }
        if (country.isEmpty()) {
            binding.editCountry.error = "Enter country"
            return false
        }
        if (!binding.checked.isChecked) {
            Toast.makeText(this, "Please agree to terms & conditions", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
