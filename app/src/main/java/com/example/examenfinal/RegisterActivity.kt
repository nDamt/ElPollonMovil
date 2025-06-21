package com.example.examenfinal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var firstNameEditText: TextInputEditText
    private lateinit var lastNameEditText: TextInputEditText
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var addressEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firstNameEditText = findViewById(R.id.firstName)
        lastNameEditText = findViewById(R.id.lastName)
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        phoneEditText = findViewById(R.id.phone)
        addressEditText = findViewById(R.id.address)
        emailEditText = findViewById(R.id.email)
        registerButton = findViewById(R.id.registerButton)
        loginButton = findViewById(R.id.loginButton)

        databaseHelper = DatabaseHelper(this)

        registerButton.setOnClickListener {
            validateAndRegister()
        }

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun validateAndRegister() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() ||
            password.isEmpty() || phone.isEmpty() || address.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Es necesario completar todos los campos para avanzar", Toast.LENGTH_SHORT).show()
        } else if (password.length < 6) {
            Toast.makeText(this, "Por favor, ingrese una contraseña de mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
        } else {
            val userId = databaseHelper.addUser(firstName, lastName, username, password, phone, address, email)
            if (userId > 0) {
                val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("firstName", firstName)
                editor.putString("lastName", lastName)
                editor.putString("username", username)
                editor.putString("password", password)
                editor.putString("phone", phone)
                editor.putString("address", address)
                editor.putString("email", email)
                editor.apply()

                Toast.makeText(this, "Te has registrado correctamente", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "El registro no se ha completado correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
