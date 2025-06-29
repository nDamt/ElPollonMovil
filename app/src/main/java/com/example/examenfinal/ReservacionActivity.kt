package com.example.examenfinal

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class ReservacionActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var etNombre: EditText
    private lateinit var etFecha: EditText
    private lateinit var etHora: EditText
    private lateinit var etNotas: EditText
    private lateinit var spPersonas: Spinner
    private lateinit var spSucursal: Spinner
    private lateinit var btnFecha: Button
    private lateinit var btnHora: Button
    private lateinit var btnConfirmar: Button
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

    private val locales = listOf(
        "Sucursal Los Olivos" to LatLng(-12.0157670148087, -77.05966105871059),
        "Sucursal Magdalena"  to LatLng(-12.101275985708028, -77.0561040923204),
        "Sucursal San Miguel" to LatLng(-12.07075226169906, -77.16414713919714),
        "Sucursal Breña"      to LatLng(-12.04154, -77.03717),
        "Sucursal Surco"      to LatLng(-12.13282, -76.99138)
    )
    private var sucursalSeleccionada: Pair<String, LatLng>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservacion)

        // Vincular vistas
        etNombre = findViewById(R.id.et_nombre)
        etFecha = findViewById(R.id.et_fecha)
        etHora = findViewById(R.id.et_hora)
        etNotas = findViewById(R.id.et_notas)
        spPersonas = findViewById(R.id.sp_personas)
        spSucursal = findViewById(R.id.sp_sucursal)
        btnFecha = findViewById(R.id.btn_fecha)
        btnHora = findViewById(R.id.btn_hora)
        btnConfirmar = findViewById(R.id.btn_confirmar)

        // --- spinner de sucursales ---
        val nombres = locales.map { it.first }
        spSucursal.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombres)

        // valor inicial
        sucursalSeleccionada = locales[0]

        spSucursal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                sucursalSeleccionada = locales[position]
                // actualizar mapa si ya está listo
                sucursalSeleccionada?.second?.let { latLng ->
                    googleMap?.clear()
                    googleMap?.addMarker(
                        MarkerOptions().position(latLng).title(sucursalSeleccionada?.first)
                    )
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnFecha.setOnClickListener { showDatePickerDialog() }
        btnHora.setOnClickListener { showTimePickerDialog() }
        btnConfirmar.setOnClickListener { confirmarReservacion() }

        // ---------- mapa ----------
        mapView = findViewById(R.id.mapView)
        val mapState = savedInstanceState?.getBundle(MAP_VIEW_BUNDLE_KEY)
        mapView.onCreate(mapState)
        mapView.getMapAsync(this)

        // Navegación inferior
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_reservacion

        bottomNavigationView.setOnItemSelectedListener { item ->
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
                R.id.nav_reservacion -> true // ya estamos aquí
                R.id.nav_locales -> {
                    startActivity(Intent(this, LocalesActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // ---------- mapa listo ----------
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // muestra la sucursal seleccionada por defecto
        sucursalSeleccionada?.second?.let {
            googleMap?.addMarker(MarkerOptions().position(it).title(sucursalSeleccionada?.first))
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 16f))
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, y, m, d ->
            etFecha.setText(String.format("%02d/%02d/%04d", d, m + 1, y))
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, h, m ->
            etHora.setText(String.format("%02d:%02d", h, m))
        }, hour, minute, true)
        timePickerDialog.show()
    }

    private fun confirmarReservacion() {
        val nombre    = etNombre.text.toString().trim()
        val fecha     = etFecha.text.toString().trim()
        val hora      = etHora.text.toString().trim()
        val personas  = spPersonas.selectedItem?.toString()?.trim() ?: ""
        val notas     = etNotas.text.toString().trim()
        val sucursal  = sucursalSeleccionada?.first ?: ""

        if (nombre.isBlank() || fecha.isBlank() || hora.isBlank() || personas.isBlank()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val mDatabase = FirebaseDatabase.getInstance().getReference("reservaciones")
        val id = mDatabase.push().key ?: return

        val data = hashMapOf(
            "nombre" to nombre,
            "fecha" to fecha,
            "hora" to hora,
            "personas" to personas,
            "notas" to notas,
            "sucursal" to sucursal,
            "timestamp" to System.currentTimeMillis()
        )

        mDatabase.child(id).setValue(data).addOnCompleteListener { t ->
            if (t.isSuccessful) {
                Toast.makeText(this, "Reservación guardada 🎉", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // MapView lifecycle
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }
}
