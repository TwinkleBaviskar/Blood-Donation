package com.example.blooddonation

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.blooddonation.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.io.FileOutputStream
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var selectedImageUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                Glide.with(this).load(it).circleCrop().into(binding.imgProfile)

                // Save temporarily in cache folder
                val inputStream = contentResolver.openInputStream(it)
                val file = File(cacheDir, "temp_profile.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                outputStream.close()
                inputStream?.close()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://blooddonation-bbec8-default-rtdb.asia-southeast1.firebasedatabase.app/")

        loadUserData()

        binding.btnChangeImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.editLastDonation.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            saveChanges()
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return
        val ref = database.getReference("users").child(userId)

        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                binding.editFullName.setText(snapshot.child("fullName").value?.toString() ?: "")
                binding.editEmail.setText(snapshot.child("email").value?.toString() ?: "")
                binding.editMobile.setText(snapshot.child("mobile").value?.toString() ?: "")
                binding.editAge.setText(snapshot.child("age").value?.toString() ?: "")
                binding.editBloodGroup.setText(snapshot.child("bloodGroup").value?.toString() ?: "")
                binding.editWeight.setText(snapshot.child("weight").value?.toString() ?: "")
                binding.editHemoglobin.setText(snapshot.child("hemoglobin").value?.toString() ?: "")
                binding.editLastDonation.setText(snapshot.child("lastDonation").value?.toString() ?: "")

                val gender = snapshot.child("gender").value?.toString()
                if (gender == "Male") binding.radioMale.isChecked = true
                else if (gender == "Female") binding.radioFemale.isChecked = true

                Glide.with(this)
                    .load(R.drawable.profile)
                    .circleCrop()
                    .into(binding.imgProfile)
            }
        }
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

    private fun saveChanges() {
        val userId = auth.currentUser?.uid ?: return
        val ref = database.getReference("users").child(userId)

        val selectedGenderId = binding.radioGender.checkedRadioButtonId
        val gender = if (selectedGenderId != -1)
            findViewById<RadioButton>(selectedGenderId).text.toString()
        else ""

        val updates = mapOf(
            "fullName" to binding.editFullName.text.toString().trim(),
            "email" to binding.editEmail.text.toString().trim(),
            "mobile" to binding.editMobile.text.toString().trim(),
            "age" to binding.editAge.text.toString().trim(),
            "gender" to gender,
            "bloodGroup" to binding.editBloodGroup.text.toString().trim(),
            "weight" to binding.editWeight.text.toString().trim(),
            "hemoglobin" to binding.editHemoglobin.text.toString().trim(),
            "lastDonation" to binding.editLastDonation.text.toString().trim()
        )

        ref.updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile Updated Successfully ✅", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ProfileScreenActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
