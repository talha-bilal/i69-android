package com.i69.ui.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.i69.R
import com.i69.data.models.market.Products
import com.i69.data.models.market.SearchProducts

class AdapterStore(
    val context: Context,
    private val productListener: SharedProductListener?,
    private var _searchProducts: SearchProducts?
) : RecyclerView.Adapter<AdapterStore.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_market_home, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemRL.setOnClickListener {
            _searchProducts?.products?.get(position)?.productId?.let { it1 ->
                productListener?.onProductClick(
                    position,
                    it1, _searchProducts?.products?.get(position)!!
                )
            }
        }

        // sets the text to the textview from our itemHolder class
        if (_searchProducts != null) {
            holder.textView.text = _searchProducts?.products?.get(position)?.title

            if (_searchProducts?.products?.get(position)?.imageUrl != null) {
                Glide.with(context).load(_searchProducts?.products?.get(position)?.imageUrl)
                    .thumbnail(
                        Glide.with(context)
                            .load(R.drawable.demo_watch)
                    )/*.apply(options)*/
                    .into(holder.imageView)
            }
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        if (_searchProducts != null)
            return _searchProducts?.products?.size!!
        else
            return 8
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val itemRL: ConstraintLayout = itemView.findViewById(R.id.clPlanType)
        val imageView: ImageView = itemView.findViewById(R.id.image_container)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val textView: TextView = itemView.findViewById(R.id.item_title)
    }

    interface SharedProductListener {

        fun onProductClick(position: Int, productId: String, product: Products)

    }
}