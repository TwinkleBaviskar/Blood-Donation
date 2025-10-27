package com.example.blooddonation

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blooddonation.databinding.ActivityRegistrationBinding
import java.util.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🗓️ Date Picker for last donation
        binding.editLastDonation.setOnClickListener {
            showDatePicker()
        }

        binding.btnsignup.setOnClickListener {
            val fullName = binding.editFullName.text.toString().trim()
            val email = binding.editEmail.text.toString().trim()
            val mobile = binding.editMobile.text.toString().trim()
            val password = binding.editPassword.text.toString().trim()
            val bloodGroup = binding.editBloodGroup.text.toString().trim()
            val country = binding.editCountry.text.toString().trim()
            val weight = binding.editWeight.text.toString().trim()
            val hemoglobin = binding.editHemoglobin.text.toString().trim()
            val lastDonation = binding.editLastDonation.text.toString().trim()

            if (validateInput(fullName, email, mobile, password, bloodGroup, country, weight, hemoglobin, lastDonation)) {
                Toast.makeText(this, "Registration successful ✅", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeScreenActivity::class.java))
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
        mobile: String,
        password: String,
        bloodGroup: String,
        country: String,
        weight: String,
        hemoglobin: String,
        lastDonation: String
    ): Boolean {
        if (fullName.isEmpty()) {
            binding.editFullName.error = "Enter full name"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editEmail.error = "Enter valid email"
            return false
        }
        if (!mobile.matches(Regex("^[6-9]\\d{9}$"))) {
            binding.editMobile.error = "Enter valid 10-digit mobile number"
            return false
        }
        if (password.length < 6) {
            binding.editPassword.error = "Password must be at least 6 characters"
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
        if (weight.isEmpty()) {
            binding.editWeight.error = "Enter weight"
            return false
        }
        if (hemoglobin.isEmpty()) {
            binding.editHemoglobin.error = "Enter hemoglobin level"
            return false
        }
        if (lastDonation.isEmpty()) {
            binding.editLastDonation.error = "Select last donation date"
            return false
        }
        if (!binding.checked.isChecked) {
            Toast.makeText(this, "Please agree to terms & conditions", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.editLastDonation.setText(date)
            },
            year, month, day
        )
        datePickerDialog.show()
    }
}
