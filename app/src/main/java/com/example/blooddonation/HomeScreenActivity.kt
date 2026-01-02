package com.example.blooddonation

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.blooddonation.fragment.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var imgHome: ImageView
    private lateinit var imgMessage: ImageView
    private lateinit var imgFindDonor: ImageView
    private lateinit var imgDonate: ImageView
    private lateinit var imgBloodBank: ImageView
    private lateinit var imgReport: ImageView
    private lateinit var imgRequest: ImageView
    private lateinit var imgProfile: ImageView
    private lateinit var imgNotification: ImageView
    private lateinit var editSearch: EditText

    private lateinit var osmMap: MapView
    private lateinit var fusedLocation: FusedLocationProviderClient

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    companion object {
        private const val LOCATION_REQUEST_CODE = 101
        private const val DATABASE_URL ="YOUR DATABASE API"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)

        // ⭐ OSM config
        Configuration.getInstance().load(
            this,
            getSharedPreferences("osm_prefs", MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = packageName

        // ⭐ Firebase init
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseDatabase.getInstance(DATABASE_URL)
        usersRef = firebaseDb.getReference("users")

        // ⭐ Init Views
        imgHome = findViewById(R.id.imgHome)
        imgMessage = findViewById(R.id.imgMessage)
        imgFindDonor = findViewById(R.id.imgFindDonor)
        imgDonate = findViewById(R.id.imgDonate)
        imgBloodBank = findViewById(R.id.imgBloodBank)
        imgReport = findViewById(R.id.imgReport)
        imgRequest = findViewById(R.id.imgRequest)
        imgProfile = findViewById(R.id.imgProfile)
        imgNotification = findViewById(R.id.imgNotification)
        editSearch = findViewById(R.id.editSearch)   // ⭐ from XML

        // ⭐ MAP
        osmMap = findViewById(R.id.osmMap)
        osmMap.setTileSource(TileSourceFactory.MAPNIK)
        osmMap.setMultiTouchControls(true)
        osmMap.isTilesScaledToDpi = true

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()

        // ⭐ SEARCH LISTENER
        setupSearchBar()

        // ⭐ Navigation Listeners
        setupNavigation()
    }

    private fun setupSearchBar() {
        // TextWatcher hata diya, ab sirf jab user "Search" dabayega tab chalega
        editSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val text = v.text.toString().trim()

                if (text.isNotEmpty()) {
                    val intent = Intent(this@HomeScreenActivity, FindDonorScreen::class.java)
                    intent.putExtra("searchQuery", text)
                    startActivity(intent)

                    // optional: text clear + focus hata do
                    v.text = null
                    v.clearFocus()
                }
                true   // action consume kar liya
            } else {
                false
            }
        }
    }

    private fun setupNavigation() {

        imgHome.setOnClickListener {
            showHome()
        }

        imgMessage.setOnClickListener {
            openFragment(MessageListFragment())
        }

        imgNotification.setOnClickListener {
            openFragment(NotificationFragment())
        }

        imgFindDonor.setOnClickListener {
            startActivity(Intent(this, FindDonorScreen::class.java))
        }

        imgDonate.setOnClickListener {
            openFragment(DonateFragment())
        }

        imgBloodBank.setOnClickListener {
            openFragment(BloodBankFragment())
        }

        imgReport.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }

        imgRequest.setOnClickListener {
            startActivity(Intent(this, RequestActivity::class.java))
        }

        imgProfile.setOnClickListener {
            startActivity(Intent(this, ProfileScreenActivity::class.java))
        }
    }

    private fun showHome() {
        findViewById<View>(R.id.scroll_content).visibility = View.VISIBLE
        findViewById<View>(R.id.fragment_container).visibility = View.GONE
    }

    private fun openFragment(fragment: Fragment) {
        try {
            findViewById<View>(R.id.scroll_content).visibility = View.GONE
            findViewById<View>(R.id.fragment_container).visibility = View.VISIBLE

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to open screen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }

        fusedLocation.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                showMap(loc.latitude, loc.longitude)
                saveUserLocation(loc.latitude, loc.longitude)
            } else {
                showMap(19.0760, 72.8777) // Default Mumbai
            }
        }
    }

    private fun saveUserLocation(lat: Double, lon: Double) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        usersRef.child(uid).updateChildren(
            mapOf(
                "latitude" to lat,
                "longitude" to lon
            )
        )
    }

    private fun showMap(lat: Double, lon: Double) {
        val controller = osmMap.controller
        controller.setZoom(17.0)
        controller.setCenter(GeoPoint(lat, lon))

        val marker = Marker(osmMap)
        marker.position = GeoPoint(lat, lon)
        marker.title = "You are here"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        osmMap.overlays.clear()
        osmMap.overlays.add(marker)
        osmMap.invalidate()
    }

    fun openMessageFragment(otherUserId: String, otherName: String) {
        openFragment(MessageFragment.newInstance(otherUserId, otherName))
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (fragment != null) {
            showHome()
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
