package com.example.examenfinal

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    private lateinit var editProfileName: EditText
    private lateinit var editProfileLastName: EditText
    private lateinit var editProfileUsername: EditText
    private lateinit var editProfilePassword: EditText
    private lateinit var editProfilePhone: EditText
    private lateinit var editProfileAddress: EditText
    private lateinit var editProfileEmail: EditText
    private lateinit var saveProfileButton: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        editProfileName = findViewById(R.id.edit_profile_name)
        editProfileLastName = findViewById(R.id.edit_profile_lastname)
        editProfileUsername = findViewById(R.id.edit_profile_username)
        editProfilePassword = findViewById(R.id.edit_profile_password)
        editProfilePhone = findViewById(R.id.edit_profile_phone)
        editProfileAddress = findViewById(R.id.edit_profile_address)
        editProfileEmail = findViewById(R.id.edit_profile_email)
        saveProfileButton = findViewById(R.id.save_profile_button)

        databaseHelper = DatabaseHelper(this)

        loadUserData()

        saveProfileButton.setOnClickListener {
            saveUserData()
        }
    }

    private fun loadUserData() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        editProfileName.setText(sharedPreferences.getString("firstName", ""))
        editProfileLastName.setText(sharedPreferences.getString("lastName", ""))
        editProfileUsername.setText(sharedPreferences.getString("username", ""))
        editProfilePassword.setText(sharedPreferences.getString("password", ""))
        editProfilePhone.setText(sharedPreferences.getString("phone", ""))
        editProfileAddress.setText(sharedPreferences.getString("address", ""))
        editProfileEmail.setText(sharedPreferences.getString("email", ""))
    }

    private fun saveUserData() {
        val firstName = editProfileName.text.toString().trim()
        val lastName = editProfileLastName.text.toString().trim()
        val username = editProfileUsername.text.toString().trim()
        val password = editProfilePassword.text.toString().trim()
        val phone = editProfilePhone.text.toString().trim()
        val address = editProfileAddress.text.toString().trim()
        val email = editProfileEmail.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() ||
            password.isEmpty() || phone.isEmpty() || address.isEmpty() || email.isEmpty()
        ) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
        } else {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("firstName", firstName)
                putString("lastName", lastName)
                putString("username", username)
                putString("password", password)
                putString("phone", phone)
                putString("address", address)
                putString("email", email)
                apply()
            }

            databaseHelper.updateUser(username, firstName, lastName, password, phone, address, email)

            Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
            loadUserData()
        }
    }
}
