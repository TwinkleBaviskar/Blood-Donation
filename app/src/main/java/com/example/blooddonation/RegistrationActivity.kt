package com.example.blooddonation

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blooddonation.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://blooddonation-bbec8-default-rtdb.asia-southeast1.firebasedatabase.app/")

        binding.editLastDonation.setOnClickListener { showDatePicker() }

        binding.btnsignup.setOnClickListener {
            val fullName = binding.editFullName.text.toString().trim()
            val email = binding.editEmail.text.toString().trim()
            val mobile = binding.editMobile.text.toString().trim()
            val password = binding.editPassword.text.toString().trim()
            val ageText = binding.editAge.text.toString().trim()
            val selectedGenderId = binding.radioGender.checkedRadioButtonId
            val gender = if (selectedGenderId != -1)
                findViewById<RadioButton>(selectedGenderId).text.toString()
            else ""
            val bloodGroup = binding.editBloodGroup.text.toString().trim()
            val weight = binding.editWeight.text.toString().trim()
            val hemoglobin = binding.editHemoglobin.text.toString().trim()
            val lastDonation = binding.editLastDonation.text.toString().trim()

            if (validateInput(fullName, email, mobile, password, ageText, gender, bloodGroup, weight, hemoglobin, lastDonation)) {
                registerUser(fullName, email, mobile, password, ageText.toInt(), gender, bloodGroup, weight, hemoglobin, lastDonation)
            }
        }

        binding.txtSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser(
        fullName: String,
        email: String,
        mobile: String,
        password: String,
        age: Int,
        gender: String,
        bloodGroup: String,
        weight: String,
        hemoglobin: String,
        lastDonation: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    val userMap = mapOf(
                        "userId" to userId,
                        "fullName" to fullName,
                        "email" to email,
                        "mobile" to mobile,
                        "age" to age,
                        "gender" to gender,
                        "bloodGroup" to bloodGroup,
                        "weight" to weight,
                        "hemoglobin" to hemoglobin,
                        "lastDonation" to lastDonation
                    )

                    database.getReference("users").child(userId).setValue(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registration Successful 🎉", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, HomeScreenActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Auth Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateInput(
        fullName: String,
        email: String,
        mobile: String,
        password: String,
        ageText: String,
        gender: String,
        bloodGroup: String,
        weight: String,
        hemoglobin: String,
        lastDonation: String
    ): Boolean {
        if (fullName.isEmpty()) { binding.editFullName.error = "Enter full name"; return false }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { binding.editEmail.error = "Enter valid email"; return false }
        if (!mobile.matches(Regex("^[6-9]\\d{9}$"))) { binding.editMobile.error = "Enter valid mobile"; return false }
        if (password.length < 6) { binding.editPassword.error = "Password must be 6+ chars"; return false }
        if (ageText.isEmpty()) { binding.editAge.error = "Enter age"; return false }

        val age = ageText.toIntOrNull()
        if (age == null || age < 18) {
            Toast.makeText(this, "You must be 18 or older to register", Toast.LENGTH_SHORT).show()
            return false
        }

        if (gender.isEmpty()) { Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show(); return false }
        if (bloodGroup.isEmpty()) { binding.editBloodGroup.error = "Enter blood group"; return false }
        if (weight.isEmpty()) { binding.editWeight.error = "Enter weight"; return false }
        if (hemoglobin.isEmpty()) { binding.editHemoglobin.error = "Enter hemoglobin"; return false }
        if (lastDonation.isEmpty()) { binding.editLastDonation.error = "Select last donation"; return false }
        if (!binding.checked.isChecked) { Toast.makeText(this, "Agree to terms", Toast.LENGTH_SHORT).show(); return false }

        return true
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            binding.editLastDonation.setText("$d/${m + 1}/$y")
        }, year, month, day).show()
    }
}
