package com.example.examenfinal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartActivity : AppCompatActivity() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartItems: MutableList<Product>
    private lateinit var profileName: TextView
    private lateinit var profileSubtitle: TextView
    private lateinit var totalAmount: TextView
    private lateinit var finalizePurchaseButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        mDatabase = FirebaseDatabase.getInstance().getReference("compras")

        recyclerView = findViewById(R.id.recycler_view_cart)
        recyclerView.layoutManager = LinearLayoutManager(this)

        profileName = findViewById(R.id.profile_name)
        profileSubtitle = findViewById(R.id.profile_subtitle)
        totalAmount = findViewById(R.id.total_amount)
        finalizePurchaseButton = findViewById(R.id.finalize_purchase_button)

        val sharedPreferences: SharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "N/A")

        profileName.text = username
        profileSubtitle.text = "Bienvenid@ a El Pollón"

        cartItems = Cart.getCartItems().toMutableList() // Asegúrate que sea mutable

        cartAdapter = CartAdapter(this, cartItems) { producto ->
            Cart.removeItem(producto)
            updateCart() // 👈 usamos el método que sincroniza todo y recalcula
            Toast.makeText(this, "${producto.name} fue eliminado del carrito", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = cartAdapter


        calculateAndDisplayTotal()

        finalizePurchaseButton.setOnClickListener {
            enviarCompraAFirebase()
        }

        setupBottomNavigation()
    }

    private fun calculateAndDisplayTotal() {
        var total = 0.0
        for (product in cartItems) {
            total += product.price * product.quantity
        }
        totalAmount.text = "Total: S/" + String.format("%.2f", total)
    }
    private fun updateCart() {
        cartItems.clear()
        cartItems.addAll(Cart.getCartItems())
        cartAdapter.notifyDataSetChanged()
        calculateAndDisplayTotal()
    }

    private fun enviarCompraAFirebase() {
        val compraId = mDatabase.push().key

        if (compraId != null) {
            val compraData = hashMapOf<String, Any>(
                "username" to "admin",
                "timestamp" to System.currentTimeMillis()
            )

            var total = 0.0
            val productosList = mutableListOf<Map<String, Any>>()

            for (product in cartItems) {
                total += product.price
                val productoData = hashMapOf<String, Any>(
                    "name" to product.name,
                    "price" to product.price,
                    "category" to product.category,
                    "cantidad" to product.quantity
                )
                productosList.add(productoData)
            }

            compraData["total"] = total
            compraData["productos"] = productosList

            mDatabase.child(compraId).setValue(compraData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Compra finalizada con éxito", Toast.LENGTH_SHORT).show()
                    calcularTiempoEstimado(cartItems.size)
                    Cart.clearCart()
                    cartAdapter.notifyDataSetChanged()
                    totalAmount.text = "Total: S/0.00"
                    startActivity(Intent(this, ConfirmacionActivity::class.java))
                } else {
                    Toast.makeText(this, "Error al finalizar compra", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.e("Firebase", "Error generando el ID de la compra")
        }
    }

    private fun calcularTiempoEstimado(cantidadProductos: Int) {
        val apiService = ApiClient.getRetrofitInstance().create(HuggingFaceApiService::class.java)

        val data = hashMapOf("cantidad_productos" to cantidadProductos)

        val call = apiService.predict(data)
        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    val tiempoEstimado = result["tiempo_estimado"] as? Double
                    Toast.makeText(this@CartActivity, "Tiempo estimado de espera: $tiempoEstimado minutos", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("API", "Error en la respuesta - Código: ${response.code()}, Cuerpo: ${response.errorBody()}")
                    Toast.makeText(this@CartActivity, "Error en la respuesta de la API", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Log.e("API", "Error en la solicitud: ${t.message}")
                Toast.makeText(this@CartActivity, "Error en la solicitud: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_cart

        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_cart -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.nav_reservacion -> {
                    startActivity(Intent(this, ReservacionActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}