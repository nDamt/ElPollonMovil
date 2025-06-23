package com.example.examenfinal

object Cart {
    private val cartItems = mutableListOf<Product>()

    fun addItem(product: Product) {
        cartItems.add(product)
    }

    fun getCartItems(): List<Product> {
        return cartItems
    }
    fun removeItem(product: Product) {
        cartItems.remove(product)
    }
    fun clearCart() {
        cartItems.clear()
    }
}
