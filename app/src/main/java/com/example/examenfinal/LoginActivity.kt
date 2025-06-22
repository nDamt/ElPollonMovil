package com.example.examenfinal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var registerText: TextView
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        registerText = findViewById(R.id.registerText)

        databaseHelper = DatabaseHelper(this)

        loginButton.setOnClickListener {
            validateAndLogin()
        }

        registerText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateAndLogin() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Todos los campos deben estar completos para proceder", Toast.LENGTH_SHORT).show()
        } else {
            val cursor: Cursor? = databaseHelper.getUser(username, password)
            if (cursor != null && cursor.moveToFirst()) {
                val editor: SharedPreferences.Editor = getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit()
                editor.putString("firstName", cursor.getString(cursor.getColumnIndexOrThrow("firstName")))
                editor.putString("lastName", cursor.getString(cursor.getColumnIndexOrThrow("lastName")))
                editor.putString("username", cursor.getString(cursor.getColumnIndexOrThrow("username")))
                editor.putString("password", cursor.getString(cursor.getColumnIndexOrThrow("password")))
                editor.putString("phone", cursor.getString(cursor.getColumnIndexOrThrow("phone")))
                editor.putString("address", cursor.getString(cursor.getColumnIndexOrThrow("address")))
                editor.putString("email", cursor.getString(cursor.getColumnIndexOrThrow("email")))
                editor.apply()

                Toast.makeText(this, "Has iniciado sesión con éxito", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Usuario no existe", Toast.LENGTH_SHORT).show()
            }
        }
    }
}