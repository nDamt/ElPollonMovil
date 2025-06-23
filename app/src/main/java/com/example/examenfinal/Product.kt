package com.example.examenfinal

data class Product(
    var name: String = "",
    var price: Double = 0.0,
    var imageResource: Int = 0,
    var category: String = "",
    var quantity: Int = 1  // 👈 agregado
)
