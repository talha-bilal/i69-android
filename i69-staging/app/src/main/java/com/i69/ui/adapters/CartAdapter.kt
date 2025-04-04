package com.i69.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.i69.R

class CartAdapter(
    val activity: Activity?,
    private val onRemoveItemFromCart: OnRemoveItemFromCart
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imgRemoveItem.setOnClickListener {
            onRemoveItemFromCart.removeItemFromCart()
        }
    }

    override fun getItemCount(): Int {
        return 10
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTitle: TextView = itemView.findViewById(R.id.itemTitle)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)
        val imgRemoveItem: ImageView = itemView.findViewById(R.id.imgRemoveItem)
    }

    interface OnRemoveItemFromCart {
        fun removeItemFromCart()
    }
}