package com.example.examenfinal

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

class LocalesActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var distanciaText: TextView

    private val PERMISSION_REQUEST_LOCATION = 1

    // 6 locales en Lima
    private val locales = listOf(
        LatLng(-12.04318, -77.02824), // Lima Centro
        LatLng(-12.12108, -77.02947), // San Isidro
        LatLng(-12.09649, -77.03518), // Miraflores
        LatLng(-12.07774, -77.05594), // Pueblo Libre
        LatLng(-12.04154, -77.03717), // Breña
        LatLng(-12.13282, -76.99138), // Surco
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locales)

        distanciaText = findViewById(R.id.distancia_text)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupBottomNavigation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Centra el mapa en Lima al iniciar
        val limaCentro = LatLng(-12.0464, -77.0428)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(limaCentro, 12f))

        // Añade marcadores de los locales
        for ((index, local) in locales.withIndex()) {
            mMap.addMarker(MarkerOptions().position(local).title("Local ${index + 1}"))
        }

        // Solicita permisos
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_LOCATION
            )
            return
        }

        mMap.isMyLocationEnabled = true

        // Muestra tu ubicación
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val miUbicacion = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(miUbicacion).title("Tú"))

                var localMasCercano: LatLng? = null
                var menorDistancia = Float.MAX_VALUE

                for (local in locales) {
                    val distancia = calcularDistancia(
                        it.latitude, it.longitude,
                        local.latitude, local.longitude
                    )
                    if (distancia < menorDistancia) {
                        menorDistancia = distancia
                        localMasCercano = local
                    }
                }

                localMasCercano?.let {
                    distanciaText.text = "📍 Local más cercano: %.2f km".format(menorDistancia)
                }
            }
        }
    }

    private fun calcularDistancia(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val loc1 = Location("").apply { latitude = lat1; longitude = lon1 }
        val loc2 = Location("").apply { latitude = lat2; longitude = lon2 }
        return loc1.distanceTo(loc2) / 1000f
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_LOCATION &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            recreate()
        } else {
            Toast.makeText(this, "Se requiere el permiso de ubicación", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_locales // ← asegúrate de tener este item en tu menú

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.nav_reservacion -> {
                    startActivity(Intent(this, ReservacionActivity::class.java))
                    true
                }
                R.id.nav_locales -> true
                else -> false
            }
        }
    }
}
