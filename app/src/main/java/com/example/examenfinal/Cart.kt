package com.example.examenfinal

class Cart private constructor() {
    private val cartItems: MutableList<Product> =
        ArrayList()

    fun addItem(product: Product) {
        cartItems.add(product)
    }

    fun getCartItems(): List<Product> {
        return cartItems
    }

    fun clearCart() {
        cartItems.clear()
    }

    companion object {
        @JvmStatic
        var instance: Cart? = null
            get() {
                if (field == null) {
                    field = Cart()
                }
                return field
            }
            private set
    }
}
