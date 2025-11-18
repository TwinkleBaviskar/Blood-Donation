package com.example.blooddonation

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class RequestActivity : AppCompatActivity() {

    private lateinit var editPatientName: EditText
    private lateinit var spinnerBloodGroup: Spinner
    private lateinit var radioGroupUrgency: RadioGroup
    private lateinit var editHospital: EditText
    private lateinit var editContact: EditText
    private lateinit var editMessage: EditText
    private lateinit var btnSendRequest: Button

    // 🔥 Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private val bloodGroups = arrayOf(
        "Select Blood Group", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        // 🩸 Firebase Init
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://blooddonation-bbec8-default-rtdb.asia-southeast1.firebasedatabase.app/")

        // 🧩 Initialize all views
        editPatientName = findViewById(R.id.editPatientName)
        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup)
        radioGroupUrgency = findViewById(R.id.radioGroupUrgency)
        editHospital = findViewById(R.id.editHospital)
        editContact = findViewById(R.id.editContact)
        editMessage = findViewById(R.id.editMessage)
        btnSendRequest = findViewById(R.id.btnSendRequest)

        // 🔽 Setup BloodGroup Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bloodGroups)
        spinnerBloodGroup.adapter = adapter

        // 🚀 Send Request Button
        btnSendRequest.setOnClickListener {
            validateAndSend()
        }
    }

    private fun validateAndSend() {
        val name = editPatientName.text.toString().trim()
        val group = spinnerBloodGroup.selectedItem.toString()
        val hospital = editHospital.text.toString().trim()
        val contact = editContact.text.toString().trim()
        val message = editMessage.text.toString().trim()

        val selectedUrgencyId = radioGroupUrgency.checkedRadioButtonId
        val urgency = if (selectedUrgencyId != -1)
            findViewById<RadioButton>(selectedUrgencyId).text.toString()
        else
            ""

        when {
            name.isEmpty() -> editPatientName.error = "Enter patient name"
            group == "Select Blood Group" -> showToast("Please select blood group")
            urgency.isEmpty() -> showToast("Please select urgency level")
            hospital.isEmpty() -> editHospital.error = "Enter hospital name"
            contact.length != 10 -> editContact.error = "Enter valid 10-digit contact number"
            else -> saveRequestToFirebase(name, group, urgency, hospital, contact, message)
        }
    }

    private fun saveRequestToFirebase(
        name: String,
        group: String,
        urgency: String,
        hospital: String,
        contact: String,
        message: String
    ) {
        val userId = auth.currentUser?.uid ?: "UnknownUser"
        val requestId = database.reference.push().key ?: UUID.randomUUID().toString()

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val requestMap = mapOf(
            "requestId" to requestId,
            "userId" to userId,
            "patientName" to name,
            "bloodGroup" to group,
            "urgency" to urgency,
            "hospital" to hospital,
            "contact" to contact,
            "message" to message,
            "timestamp" to timestamp
        )

        val requestRef = database.getReference("requests").child(requestId)

        requestRef.setValue(requestMap)
            .addOnSuccessListener {
                showSuccessDialog(name, group, urgency, hospital, contact, message)
            }
            .addOnFailureListener {
                showToast("Failed to send request: ${it.message}")
            }
    }

    private fun showSuccessDialog(
        name: String,
        group: String,
        urgency: String,
        hospital: String,
        contact: String,
        message: String
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("✅ Request Sent")
        builder.setMessage(
            """
            Blood Request Sent Successfully!
            
            👤 Name: $name
            🩸 Blood Group: $group
            ⚡ Urgency: $urgency
            🏥 Hospital: $hospital
            📞 Contact: $contact
            
            ${if (message.isNotEmpty()) "💬 Message: $message" else ""}
            """.trimIndent()
        )
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            clearFields()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun clearFields() {
        editPatientName.text.clear()
        spinnerBloodGroup.setSelection(0)
        radioGroupUrgency.clearCheck()
        editHospital.text.clear()
        editContact.text.clear()
        editMessage.text.clear()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
