package com.example.examenfinal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val context: Context,
    private val productList: MutableList<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val productListFull: List<Product> = ArrayList(productList)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.name
        holder.productPrice.text = "S/${product.price}"
        holder.productImage.setImageResource(product.imageResource)

        holder.addToCartButton.setOnClickListener {
            Cart.addItem(product)
            Toast.makeText(context, "${product.name} se agregó al carrito", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = productList.size

    fun filter(text: String) {
        productList.clear()
        if (text.isEmpty()) {
            productList.addAll(productListFull)
        } else {
            val lowerText = text.lowercase()
            for (product in productListFull) {
                if (product.name.lowercase().contains(lowerText)) {
                    productList.add(product)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun filterByCategory(category: String) {
        productList.clear()
        if (category.isEmpty()) {
            productList.addAll(productListFull)
        } else {
            for (product in productListFull) {
                if (product.category.equals(category, ignoreCase = true)) {
                    productList.add(product)
                }
            }
        }
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.producto_image)
        val productName: TextView = itemView.findViewById(R.id.producto_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val addToCartButton: ImageButton = itemView.findViewById(R.id.add_to_cart_button)
    }
}
