package com.example.blooddonation

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.TextView
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
        database = FirebaseDatabase.getInstance("YOUR DATABASE API")

        setupBloodDropdown()
        setupHemoglobinDropdown()
        setupImeNavigation()
        setupInputFilters()

        // Ensure dropDown width/offset set after layout
        binding.editBloodGroup.post {
            applyPopupInsetForField(binding.editBloodGroup, sideInsetDp = 16, maxHeightDp = 220)
        }
        binding.editHemoglobin.post {
            applyPopupInsetForField(binding.editHemoglobin, sideInsetDp = 16, maxHeightDp = 200)
        }

        binding.editLastDonation.setOnClickListener { showDatePicker() }

        binding.btnsignup.setOnClickListener {
            val fullName = binding.editFullName.text.toString().trim()
            val email = binding.editEmail.text.toString().trim()
            val mobile = binding.editMobile.text.toString().trim()
            val password = binding.editPassword.text.toString().trim()
            val ageText = binding.editAge.text.toString().trim()
            val selectedGenderId = binding.radioGender.checkedRadioButtonId
            val gender = if (selectedGenderId != -1) findViewById<RadioButton>(selectedGenderId).text.toString() else ""
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

    private fun setupBloodDropdown() {
        val bloodList = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bloodList)
        binding.editBloodGroup.setAdapter(adapter)
        binding.editBloodGroup.threshold = 1

        binding.editBloodGroup.setOnClickListener { binding.editBloodGroup.showDropDown() }
        binding.editBloodGroup.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) binding.editBloodGroup.showDropDown() }
    }

    private fun setupHemoglobinDropdown() {
        val hemoList = listOf("7.0", "8.0", "9.0", "10.0", "10.5", "11.0", "11.5", "12.0", "12.5", "13.0", "13.5", "14.0", "15.0")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, hemoList)
        binding.editHemoglobin.setAdapter(adapter)
        binding.editHemoglobin.threshold = 1

        binding.editHemoglobin.setOnClickListener { binding.editHemoglobin.showDropDown() }
        binding.editHemoglobin.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) binding.editHemoglobin.showDropDown() }
    }

    private fun applyPopupInsetForField(field: android.widget.AutoCompleteTextView, sideInsetDp: Int = 16, maxHeightDp: Int = 220) {
        val density = resources.displayMetrics.density
        val sideInsetPx = (sideInsetDp * density).toInt()
        val maxHeightPx = (maxHeightDp * density).toInt()

        val fieldWidth = field.width
        if (fieldWidth > 0) {
            val desiredWidth = fieldWidth - (2 * sideInsetPx)
            if (desiredWidth > 0) {
                field.dropDownWidth = desiredWidth
                // offset popup from the field's left by sideInsetPx so there is gap on both sides
                field.dropDownHorizontalOffset = sideInsetPx
            } else {
                // fallback to wrap_content
                field.dropDownWidth = android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
        // set maximum popup height (keeps it from filling screen)
        field.dropDownHeight = maxHeightPx
        field.dropDownVerticalOffset = (6 * density).toInt()
    }

    private fun setupImeNavigation() {
        val fields = listOf(
            binding.editFullName,
            binding.editMobile,
            binding.editEmail,
            binding.editPassword,
            binding.editAge,
            binding.editBloodGroup,
            binding.editWeight,
            binding.editHemoglobin
        )

        for (i in fields.indices) {
            val nextIndex = i + 1
            fields[i].setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (nextIndex < fields.size) fields[nextIndex].requestFocus()
                    else binding.editLastDonation.requestFocus()
                    true
                } else false
            }
        }
    }

    private fun setupInputFilters() {
        binding.editWeight.filters = arrayOf(DecimalDigitsInputFilter(3, 1))
        binding.editHemoglobin.filters = arrayOf(DecimalDigitsInputFilter(2, 1))
    }

    class DecimalDigitsInputFilter(private val digitsBeforeZero: Int, private val digitsAfterZero: Int) : InputFilter {
        private val pattern = Regex("^[0-9]{0,$digitsBeforeZero}+((\\.[0-9]{0,$digitsAfterZero})?)\$")

        override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
            try {
                val newVal = (dest?.substring(0, dstart) ?: "") + (source?.subSequence(start, end) ?: "") + (dest?.substring(dend) ?: "")
                if (newVal.isEmpty()) return null
                if (newVal == ".") return ""
                return if (pattern.matches(newVal)) null else ""
            } catch (e: Exception) {
                return ""
            }
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
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
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
                        startActivity(Intent(this, LoginActivity::class.java))
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
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { binding.editEmail.error = "Enter valid email"; return false }
        if (!mobile.matches(Regex("^[6-9]\\d{9}$"))) { binding.editMobile.error = "Enter valid mobile"; return false }
        if (password.length < 6) { binding.editPassword.error = "Password must be 6+ chars"; return false }
        if (ageText.isEmpty()) { binding.editAge.error = "Enter age"; return false }

        val age = ageText.toIntOrNull()
        if (age == null || age < 18 || age > 120) {
            Toast.makeText(this, "Enter valid age (18 - 120)", Toast.LENGTH_SHORT).show()
            return false
        }

        if (gender.isEmpty()) { Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show(); return false }
        if (bloodGroup.isEmpty()) { binding.editBloodGroup.error = "Enter blood group"; return false }

        val weightVal = weight.toDoubleOrNull()
        if (weight.isEmpty() || weightVal == null || weightVal <= 0.0 || weightVal > 500.0) {
            binding.editWeight.error = "Enter valid weight"
            return false
        }

        val hemoVal = hemoglobin.toDoubleOrNull()
        if (hemoglobin.isEmpty() || hemoVal == null || hemoVal <= 0.0 || hemoVal > 30.0) {
            binding.editHemoglobin.error = "Enter valid hemoglobin"
            return false
        }

        if (lastDonation.isEmpty()) { binding.editLastDonation.error = "Select donation date"; return false }
        if (!binding.checked.isChecked) { Toast.makeText(this, "Agree to terms", Toast.LENGTH_SHORT).show(); return false }

        return true
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, y, m, d -> binding.editLastDonation.setText("$d/${m + 1}/$y") },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
