package com.example.examenfinal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private val context: Context,
    private val cartItems: List<Product>,
    private val onDeleteClick: (Product) -> Unit,
    private val onQuantityChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartItems[position]
        holder.productName.text = product.name
        holder.productPrice.text = "S/${String.format("%.2f", product.price)}"
        holder.productImage.setImageResource(product.imageResource)
        holder.productQuantity.text = "${product.quantity}"

        holder.deleteButton.setOnClickListener {
            onDeleteClick(product)
        }

        holder.btnSumar.setOnClickListener {
            product.quantity++
            holder.productQuantity.text = "${product.quantity}"
            onQuantityChanged()
        }

        holder.btnRestar.setOnClickListener {
            if (product.quantity > 1) {
                product.quantity--
                holder.productQuantity.text = "${product.quantity}"
                onQuantityChanged()
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.producto_image)
        val productName: TextView = itemView.findViewById(R.id.producto_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_product_button)
        val productQuantity: TextView = itemView.findViewById(R.id.product_quantity)
        val btnSumar: ImageButton = itemView.findViewById(R.id.button_increase)
        val btnRestar: ImageButton = itemView.findViewById(R.id.button_decrease)
    }
}
