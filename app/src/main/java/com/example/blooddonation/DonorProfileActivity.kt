package com.example.blooddonation

import DonorModel
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class DonorProfileActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_profile)

        Configuration.getInstance().load(
            this,
            getSharedPreferences("osm_prefs", MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = packageName

        val backButton: ImageView = findViewById(R.id.backButton)
        val profileImage: ImageView = findViewById(R.id.profileImage)
        val profileName: TextView = findViewById(R.id.profileName)
        val lastDonation: TextView = findViewById(R.id.lastDonation)
        val callButton: TextView = findViewById(R.id.callButton)
        val requestButton: TextView = findViewById(R.id.requestButton)
        val donatedValue: TextView = findViewById(R.id.donatedValue)
        val bloodTypeValue: TextView = findViewById(R.id.bloodTypeValue)
        val lifeSavedValue: TextView = findViewById(R.id.lifeSavedValue)

        mapView = findViewById(R.id.osmDonorMap)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.isTilesScaledToDpi = true


        val donor = intent.getSerializableExtra("donorData") as DonorModel

        profileName.text = donor.fullName ?: "Unknown Donor"
        lastDonation.text = "Last Donation: ${donor.lastDonation ?: "-"}"
        bloodTypeValue.text = donor.bloodGroup ?: "-"
        donatedValue.text = donor.totalDonations?.toString() ?: "0"
        lifeSavedValue.text = donor.livesSaved?.toString() ?: "0"

        if (!donor.profileImage.isNullOrEmpty()) {
            Glide.with(this)
                .load(donor.profileImage)
                .placeholder(R.drawable.profile)
                .into(profileImage)
        } else {
            profileImage.setImageResource(R.drawable.profile)
        }

        if (donor.latitude != null && donor.longitude != null) {
            showDonorLocation(donor.latitude, donor.longitude, donor.fullName ?: "")
        } else {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            // default India center – taaki weird pattern na dikhe
            showDonorLocation(20.5937, 78.9629, "Location not available")
        }


        backButton.setOnClickListener { finish() }
        callButton.setOnClickListener {
            donor.mobile?.let {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$it")))
            } ?: Toast.makeText(this, "No phone number", Toast.LENGTH_SHORT).show()
        }


        requestButton.setOnClickListener {
            val intent = Intent(this, RequestActivity::class.java)
            intent.putExtra("donorId", donor.userId)
            intent.putExtra("donorName", donor.fullName)
            intent.putExtra("donorBloodGroup", donor.bloodGroup)
            intent.putExtra("donorMobile", donor.mobile)
            intent.putExtra("donorLastDonation", donor.lastDonation)
            startActivity(intent)
        }


        if (donor.userId == FirebaseAuth.getInstance().currentUser?.uid) {
            callButton.visibility = View.GONE
            requestButton.visibility = View.GONE
        }
    }

    private fun showDonorLocation(lat: Double, lng: Double, name: String) {
        val controller = mapView.controller
        controller.setZoom(16.0)
        controller.setCenter(GeoPoint(lat, lng))

        val marker = Marker(mapView)
        marker.position = GeoPoint(lat, lng)
        marker.title = name
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        mapView.overlays.clear()
        mapView.overlays.add(marker)

        mapView.invalidate()
    }
}
