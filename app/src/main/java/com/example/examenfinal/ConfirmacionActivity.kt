package com.example.examenfinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmacionActivity : AppCompatActivity() {

    private lateinit var backToCartButton: Button
    private lateinit var tiempoEstimadoText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacion)

        backToCartButton = findViewById(R.id.button_volver_carrito)
        tiempoEstimadoText = findViewById(R.id.tiempo_estimado_text)

        // Obtener la cantidad de productos enviada desde CartActivity
        val cantidadProductos = intent.getIntExtra("cantidad_productos", 0)

        // Llamada a la API de Hugging Face
        val apiService = ApiClient.getRetrofitInstance().create(HuggingFaceApiService::class.java)
        val data = hashMapOf("cantidad_productos" to cantidadProductos)

        val call = apiService.predict(data)
        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    val tiempoEstimado = result["tiempo_estimado"] as? Double
                    tiempoEstimadoText.text = "Tiempo estimado: ${tiempoEstimado?.toInt()} minutos"
                } else {
                    tiempoEstimadoText.text = "Error obteniendo el tiempo estimado"
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                tiempoEstimadoText.text = "Error en la conexión: ${t.message}"
            }
        })

        // Acción del botón
        backToCartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
