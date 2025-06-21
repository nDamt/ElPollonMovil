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
    private lateinit var btnPostres: Button

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
        btnPostres = findViewById(R.id.btn_postres)

        val sharedPreferences: SharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "N/A")
        profileName.text = username
        profileSubtitle.text = "Bienvenid@ a Cafeteria Fito Espinoza"

        // ✅ Inicializar y poblar SQLite
        val dbHelper = SQLiteHelper(this)

        // ✅ Solo insertar si aún no hay datos
        if (dbHelper.obtenerProductos().isEmpty()) {
            val productos = listOf(
                Product("Café Espresso", 7.00, R.drawable.ic_product, "Bebidas"),
                Product("Café Americano", 8.00, R.drawable.ic_product, "Bebidas"),
                Product("Café Cortado", 9.00, R.drawable.ic_product, "Bebidas"),
                Product("Café Latte", 11.00, R.drawable.ic_product, "Bebidas"),
                Product("Infusiones", 7.00, R.drawable.ic_product, "Bebidas"),
                Product("Limonada Frozen", 12.00, R.drawable.frio, "Bebidas"),
                Product("Coca-Cola personal", 5.00, R.drawable.frio, "Bebidas"),
                Product("Gasificado de Maracuyá", 5.00, R.drawable.frio, "Bebidas"),
                Product("Smoothie", 7.5, R.drawable.frio, "Bebidas"),
                Product("Piña Colada sin Alcohol", 12.00, R.drawable.frio, "Bebidas"),
                Product("Hamburguesa", 8.00, R.drawable.comida, "Platos"),
                Product("Ensalada Cesar", 15.00, R.drawable.comida, "Platos"),
                Product("Pizza", 12.00, R.drawable.comida, "Platos"),
                Product("Pastas", 20.00, R.drawable.comida, "Platos"),
                Product("Sandwich de pavo criollo", 20.00, R.drawable.comida, "Platos"),
                Product("Provoleta", 38.00, R.drawable.comida, "Platos"),
                Product("Sushi", 15.00, R.drawable.comida, "Platos"),
                Product("Pollo Asado", 16.00, R.drawable.comida, "Platos"),
                Product("Tagliatelle al pesto con milanesa de carne y queso parmesano", 18.00, R.drawable.comida, "Platos"),
                Product("Filete de Res", 14.00, R.drawable.comida, "Platos"),
                Product("Carrot Cake", 18.00, R.drawable.postre, "Postres"),
                Product("Torta de Chocolate", 18.00, R.drawable.postre, "Postres"),
                Product("Pie de Limón", 16.00, R.drawable.postre, "Postres"),
                Product("Pie de Maracuyá", 16.00, R.drawable.postre, "Postres"),
                Product("Crema Volteada", 16.00, R.drawable.postre, "Postres"),
                Product("Cheesecake de Frutos Rojos", 18.00, R.drawable.postre, "Postres"),
                Product("Helado(1 bola)", 12.00, R.drawable.postre, "Postres"),
                Product("Helado(2 bola)", 16.00, R.drawable.postre, "Postres"),
                Product("Cake de Naranja", 12.00, R.drawable.postre, "Postres"),
                Product("Cake de Arandanos", 12.00, R.drawable.postre, "Postres")
            )
            for (producto in productos) {
                dbHelper.insertarProducto(producto.name, producto.price, producto.category)
            }
        }

        // ✅ Leer productos desde SQLite
        productList = dbHelper.obtenerProductos().toMutableList()

        productAdapter = ProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                productAdapter.filter(newText ?: "")
                return true
            }
        })

        btnBrasas.setOnClickListener { productAdapter.filterByCategory("Brasas") }
        btnParrillas.setOnClickListener { productAdapter.filterByCategory("Parrillas") }
        btnPostres.setOnClickListener { productAdapter.filterByCategory("Postres") }

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
