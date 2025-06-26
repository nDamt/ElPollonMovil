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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

class LocalesActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var distanciaText: TextView
    private var miUbicacion: LatLng? = null

    private val PERMISSION_REQUEST_LOCATION = 1

    private val locales = listOf(
        Pair("Sucursal Los Olivos", LatLng(-12.0157670148087, -77.05966105871059)),
        Pair("Sucursal Magdalena", LatLng(-12.101275985708028, -77.0561040923204)),
        Pair("Sucursal San Miguel", LatLng(-12.07075226169906, -77.16414713919714)),
        Pair("Sucursal Santa Anita", LatLng(-12.012290361102261, -76.98794016670966)),
        Pair("Sucursal Breña", LatLng(-12.04154, -77.03717)),
        Pair("Sucursal Surco", LatLng(-12.13282, -76.99138))
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

        val lima = LatLng(-12.0464, -77.0428)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 12f))

        for ((nombre, local) in locales) {
            mMap.addMarker(MarkerOptions().position(local).title(nombre))
        }

        mMap.setOnMarkerClickListener { marker ->
            if (marker.title != "Tú" && miUbicacion != null) {
                val distancia = calcularDistancia(
                    miUbicacion!!.latitude,
                    miUbicacion!!.longitude,
                    marker.position.latitude,
                    marker.position.longitude
                )
                Toast.makeText(
                    this,
                    "Distancia a ${marker.title}: %.2f km".format(distancia),
                    Toast.LENGTH_LONG
                ).show()
            }
            false // para que igual muestre el info window
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_LOCATION
            )
            return
        }

        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                miUbicacion = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(miUbicacion!!).title("Tú"))

                var localMasCercano: Pair<String, LatLng>? = null
                var menorDistancia = Float.MAX_VALUE

                for ((nombre, local) in locales) {
                    val distancia = calcularDistancia(
                        it.latitude, it.longitude,
                        local.latitude, local.longitude
                    )
                    if (distancia < menorDistancia) {
                        menorDistancia = distancia
                        localMasCercano = Pair(nombre, local)
                    }
                }

                localMasCercano?.let {
                    distanciaText.text = "📍 Local más cercano: ${it.first} - %.2f km".format(menorDistancia)
                }
            }
        }
    }

    private fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
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
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            recreate()
        } else {
            Toast.makeText(this, "Se requiere el permiso de ubicación", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_locales

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
                R.id.nav_reservacion -> {
                    startActivity(Intent(this, ReservacionActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.nav_locales -> true
                else -> false
            }
        }
    }
}
