package com.example.examenfinal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: MutableList<Product>
    private lateinit var profileName: TextView
    private lateinit var profileSubtitle: TextView
    private lateinit var searchView: SearchView
    private lateinit var btnBrasas: Button
    private lateinit var btnParrillas: Button
    private lateinit var btnBebidas: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        profileName = findViewById(R.id.profile_name)
        profileSubtitle = findViewById(R.id.profile_subtitle)
        searchView = findViewById(R.id.search_view)
        btnBrasas = findViewById(R.id.btn_brasas)
        btnParrillas = findViewById(R.id.btn_parrillas)
        btnBebidas = findViewById(R.id.btn_bebidas)

        val sharedPreferences: SharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "N/A")
        profileName.text = username
        profileSubtitle.text = "Bienvenid@ a El Pollón"

        // ✅ Inicializar SQLiteHelper
        val dbHelper = SQLiteHelper(this)

        // ✅ Insertar datos solo si la tabla está vacía
        if (dbHelper.obtenerProductos().isEmpty()) {
            val bebidas = listOf(
                Product("Café Espresso", 7.00, R.drawable.cafeespre, "Bebidas"),
                Product("Café Americano", 8.00, R.drawable.cafeame, "Bebidas"),
                Product("Café Latte", 9.50, R.drawable.cafelatte, "Bebidas"),
                Product("Infusión de Manzanilla", 6.00, R.drawable.manzanilla, "Bebidas"),
                Product("Limonada Frozen", 12.00, R.drawable.limonadafro, "Bebidas"),
                Product("Coca-Cola", 5.00, R.drawable.cocacola, "Bebidas"),
                Product("Agua Mineral", 4.00, R.drawable.agua, "Bebidas"),
                Product("Smoothie de Fresa", 8.50, R.drawable.fresa, "Bebidas"),
                Product("Piña Colada sin Alcohol", 12.00, R.drawable.pinacola, "Bebidas")
            )
            val parrillas = listOf(
                Product("Parrilla Mixta", 35.00, R.drawable.parrillamixta, "Parrillas"),
                Product("Costillas BBQ", 32.00, R.drawable.costillasbbq, "Parrillas"),
                Product("Chorizo Parrillero", 14.00, R.drawable.chorizo, "Parrillas"),
                Product("Brochetas de Carne", 20.00, R.drawable.brochetas, "Parrillas"),
                Product("Asado de Tira", 38.00, R.drawable.asadodetira, "Parrillas")
            )
            val brasas = listOf(
                Product("Pollo a la Brasa", 16.00, R.drawable.pollobrasa, "Brasas"),
                Product("Anticuchos", 12.00, R.drawable.anticucho, "Brasas"),
                Product("Alitas BBQ", 15.00, R.drawable.alitasbbq, "Brasas"),
                Product("Sandwich de Lomo", 18.00, R.drawable.sandwich, "Brasas"),
                Product("Papas Bravas", 9.00, R.drawable.papasbra, "Brasas")
            )

            val todosLosProductos = bebidas + parrillas + brasas
            for (producto in todosLosProductos) {
                dbHelper.insertarProducto(producto.name, producto.price, producto.category, producto.imageResource)
            }
        }

        // ✅ Obtener productos de la base de datos
        productList = dbHelper.obtenerProductos().toMutableList()

        // ✅ Inicializar adaptador y RecyclerView
        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        productAdapter.filterByCategory("Brasas")

        // ✅ Búsqueda
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                productAdapter.filter(newText ?: "")
                return true
            }
        })

        // ✅ Botones de categoría
        btnBrasas.setOnClickListener { productAdapter.filterByCategory("Brasas") }
        btnParrillas.setOnClickListener { productAdapter.filterByCategory("Parrillas") }
        btnBebidas.setOnClickListener { productAdapter.filterByCategory("Bebidas") }

        // ✅ Navegación inferior
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
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
                else -> false
            }
        }
    }
}