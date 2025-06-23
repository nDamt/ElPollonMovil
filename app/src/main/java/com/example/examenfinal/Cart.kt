package com.example.examenfinal

object Cart {
    private val cartItems = mutableListOf<Product>()

    fun addItem(product: Product) {
        val existingProduct = cartItems.find { it.name == product.name }
        if (existingProduct != null) {
            existingProduct.quantity++
        } else {
            cartItems.add(product.copy(quantity = 1))
        }
    }


    fun getCartItems(): List<Product> {
        return cartItems
    }
    fun removeItem(product: Product) {
        val existingProduct = cartItems.find { it.name == product.name }
        if (existingProduct != null) {
            if (existingProduct.quantity > 1) {
                existingProduct.quantity--
            } else {
                cartItems.remove(existingProduct)
            }
        }
    }

    fun clearCart() {
        cartItems.clear()
    }
}
