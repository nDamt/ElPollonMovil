package com.example.examenfinal

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                firstName TEXT,
                lastName TEXT,
                username TEXT,
                password TEXT,
                phone TEXT,
                address TEXT,
                email TEXT
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    fun addUser(
        firstName: String,
        lastName: String,
        username: String,
        password: String,
        phone: String,
        address: String,
        email: String
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("firstName", firstName)
            put("lastName", lastName)
            put("username", username)
            put("password", password)
            put("phone", phone)
            put("address", address)
            put("email", email)
        }
        return db.insert("users", null, values)
    }

    fun getUser(username: String, password: String): Cursor {
        val db = readableDatabase
        return db.rawQuery(
            "SELECT * FROM users WHERE username = ? AND password = ?",
            arrayOf(username, password)
        )
    }

    fun updateUser(
        username: String,
        firstName: String,
        lastName: String,
        password: String,
        phone: String,
        address: String,
        email: String
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("firstName", firstName)
            put("lastName", lastName)
            put("password", password)
            put("phone", phone)
            put("address", address)
            put("email", email)
        }
        return db.update("users", values, "username = ?", arrayOf(username))
    }

    // ✅ Nuevo método para actualizar incluso si cambia el username
    fun updateUserByUsername(
        originalUsername: String,
        newUsername: String,
        firstName: String,
        lastName: String,
        password: String,
        phone: String,
        address: String,
        email: String
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("firstName", firstName)
            put("lastName", lastName)
            put("username", newUsername)
            put("password", password)
            put("phone", phone)
            put("address", address)
            put("email", email)
        }
        val rows = db.update("users", values, "username = ?", arrayOf(originalUsername))
        return rows > 0
    }

    companion object {
        private const val DATABASE_NAME = "pollon.db"
        private const val DATABASE_VERSION = 1
    }
}
