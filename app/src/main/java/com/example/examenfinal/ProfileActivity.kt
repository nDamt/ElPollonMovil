package com.example.examenfinal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileName: TextView
    private lateinit var profileLastName: TextView
    private lateinit var profileUsername: TextView
    private lateinit var profilePassword: TextView
    private lateinit var profilePhone: TextView
    private lateinit var profileAddress: TextView
    private lateinit var profileEmail: TextView
    private lateinit var editProfileButton: Button
    private lateinit var salirButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profileName = findViewById(R.id.profile_name)
        profileLastName = findViewById(R.id.profile_lastname)
        profileUsername = findViewById(R.id.profile_username)
        profilePassword = findViewById(R.id.profile_password)
        profilePhone = findViewById(R.id.profile_phone)
        profileAddress = findViewById(R.id.profile_address)
        profileEmail = findViewById(R.id.profile_email)
        editProfileButton = findViewById(R.id.edit_profile_button)
        salirButton = findViewById(R.id.exitButton)

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val firstName = sharedPreferences.getString("firstName", "N/A")
        val lastName = sharedPreferences.getString("lastName", "N/A")
        val username = sharedPreferences.getString("username", "N/A")
        val password = sharedPreferences.getString("password", "N/A")
        val phone = sharedPreferences.getString("phone", "N/A")
        val address = sharedPreferences.getString("address", "N/A")
        val email = sharedPreferences.getString("email", "N/A")

        profileName.text = firstName
        profileLastName.text = lastName
        profileUsername.text = username
        profilePassword.text = password
        profilePhone.text = phone
        profileAddress.text = address
        profileEmail.text = email

        editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        salirButton.setOnClickListener {
            startActivity(Intent(this, InicioActivity::class.java))
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_profile

        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                R.id.nav_profile -> true
                R.id.nav_reservacion -> {
                    startActivity(Intent(this, ReservacionActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
