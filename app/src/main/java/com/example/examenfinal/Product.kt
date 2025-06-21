package com.example.examenfinal

class Product {
    var name: String? = null
        private set
    var price: Double = 0.0
        private set
    var imageResource: Int = 0
        private set
    var category: String? = null
        private set

    // Constructor vacío requerido por Firebase
    constructor()

    constructor(name: String?, price: Double, imageResource: Int, category: String?) {
        this.name = name
        this.price = price
        this.imageResource = imageResource
        this.category = category
    }
}
