package com.i69.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.i69.R
import com.i69.data.models.market.Products

class ProductsAdapter(
    val activity: Activity?,
    private val productListener: SharedProductListener?,
) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {
    private val productList = mutableListOf<Products>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_market_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemRL.setOnClickListener {
            productList[position].productId?.let { it1 ->
                productListener?.onProductClick(
                    position,
                    it1, productList[position]
                )
            }
        }

        holder.textView.text = productList[position].title
        holder.itemPrice.text =
            productList[position].currencySymbol + productList[position].resellPrice.toString()
        if (!productList[position].score.isNullOrEmpty())
            holder.ratingBar.rating = productList[position].score!!.toFloat()

        if (productList[position].imageUrl != null) {
            Glide.with(activity!!).load(productList[position].imageUrl)
                .thumbnail(
                    Glide.with(activity)
                        .load(R.drawable.demo_watch)
                )
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun addProducts(products: MutableList<Products>) {
        productList.addAll(products)
        notifyDataSetChanged()
    }

    fun clearAdapter() {
        productList.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val itemRL: ConstraintLayout = itemView.findViewById(R.id.clPlanType)
        val imageView: ImageView = itemView.findViewById(R.id.image_container)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val textView: TextView = itemView.findViewById(R.id.item_title)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
    }

    interface SharedProductListener {
        fun onProductClick(position: Int, productId: String, product: Products)
    }
}