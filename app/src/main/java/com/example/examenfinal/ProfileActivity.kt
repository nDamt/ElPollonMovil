package com.example.examenfinal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.text.method.PasswordTransformationMethod
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
                R.id.nav_locales -> {
                    startActivity(Intent(this, LocalesActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()
    }

    private fun loadProfileData() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        profileName.text = sharedPreferences.getString("firstName", "N/A")
        profileLastName.text = sharedPreferences.getString("lastName", "N/A")
        profileUsername.text = sharedPreferences.getString("username", "N/A")
        profilePassword.text = sharedPreferences.getString("password", "N/A")
        profilePassword.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
        profilePhone.text = sharedPreferences.getString("phone", "N/A")
        profileAddress.text = sharedPreferences.getString("address", "N/A")
        profileEmail.text = sharedPreferences.getString("email", "N/A")
    }
}
