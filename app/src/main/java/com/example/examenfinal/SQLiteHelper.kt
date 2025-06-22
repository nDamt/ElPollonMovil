package com.example.examenfinal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, "productos.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE productos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                precio REAL,
                categoria TEXT,
                imagen INTEGER
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS productos")
        onCreate(db)
    }

    fun insertarProducto(nombre: String, precio: Double, categoria: String, imagen: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("precio", precio)
            put("categoria", categoria)
            put("imagen", imagen)
        }
        return db.insert("productos", null, values)
    }

    fun obtenerProductos(): List<Product> {
        val lista = mutableListOf<Product>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM productos", null)

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
                val imagen = cursor.getInt(cursor.getColumnIndexOrThrow("imagen"))
                lista.add(Product(nombre, precio, imagen, categoria))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }
}
