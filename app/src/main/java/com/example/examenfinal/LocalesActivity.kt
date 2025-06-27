package com.example.examenfinal

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LocalesActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var distanciaText: TextView
    private var miUbicacion: LatLng? = null
    private var rutaActual: Polyline? = null

    private val PERMISSION_REQUEST_LOCATION = 1

    private val locales = listOf(
        Pair("Sucursal Los Olivos", LatLng(-12.0157670148087, -77.05966105871059)),
        Pair("Sucursal Magdalena", LatLng(-12.101275985708028, -77.0561040923204)),
        Pair("Sucursal San Miguel", LatLng(-12.07075226169906, -77.16414713919714)),
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
                distanciaText.text = "📍 Ruta a ${marker.title} - %.2f km".format(distancia)
                trazarRuta(marker.position)
            }
            false
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

                mMap.addMarker(
                    MarkerOptions().position(miUbicacion!!).title("Tú")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                )

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
                    trazarRuta(it.second)
                }
            }
        }
    }

    private fun trazarRuta(destino: LatLng) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ORSService::class.java)

        val origen = miUbicacion ?: return
        val req = RouteRequest(
            listOf(
                listOf(origen.longitude, origen.latitude),
                listOf(destino.longitude, destino.latitude)
            )
        )

        service.getRoute(req).enqueue(object : Callback<ORSResponse> {
            override fun onResponse(call: Call<ORSResponse>, response: Response<ORSResponse>) {
                response.body()?.features?.get(0)?.geometry?.coordinates?.let { coords ->
                    val path = coords.map { LatLng(it[1], it[0]) }

                    // Elimina la ruta anterior
                    rutaActual?.remove()

                    // Dibuja nueva ruta
                    rutaActual = mMap.addPolyline(
                        PolylineOptions()
                            .addAll(path)
                            .color(Color.BLUE)
                            .width(6f)
                    )
                }
            }

            override fun onFailure(call: Call<ORSResponse>, t: Throwable) {
                Toast.makeText(this@LocalesActivity, "Error en ruta: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
