package com.example.examenfinal

class Compra {
    var products: List<Product>? = null
        private set
    var username: String? = null
        private set
    var timestamp: Long = 0
        private set

    constructor()

    constructor(products: List<Product>?, username: String?, timestamp: Long) {
        this.products = products
        this.username = username
        this.timestamp = timestamp
    }
}