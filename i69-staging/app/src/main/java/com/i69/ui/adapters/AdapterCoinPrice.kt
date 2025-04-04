package com.i69.ui.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.i69.applocalization.AppStringConstant
import com.i69.data.remote.responses.CoinPrice
import com.i69.databinding.ItemPurchaseCoinsNewBinding

class AdapterCoinPrice(
    var context: Context,
    private var itemList: MutableList<CoinPrice>,
    private var appStringConst: AppStringConstant,
    private var coinPriceInterface: CoinPriceInterface
) : RecyclerView.Adapter<AdapterCoinPrice.CoinPriceViewHolder>() {


    inner class CoinPriceViewHolder(val viewBinding: ItemPurchaseCoinsNewBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        val numberOfCoins = viewBinding.numberOfCoins
        val price = viewBinding.price
        val priceSmall = viewBinding.priceSmall
        val salePrice = viewBinding.salePrice
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CoinPriceViewHolder = CoinPriceViewHolder(ItemPurchaseCoinsNewBinding.inflate(
        LayoutInflater.from(context), parent, false
    ).apply {
        stringConstant = appStringConst
    })

    override fun onBindViewHolder(holder: CoinPriceViewHolder, position: Int) {
        val coinPrice = itemList[holder.bindingAdapterPosition]
        holder.numberOfCoins.text = coinPrice.coinsCount
        val value = coinPrice.discountedPrice.toFloat()
        val valueTruncated = value.toInt()
        val value2 = valueTruncated.toFloat()
        val value3 = value - value2
        val s = String.format("%.2f", value3)
        holder.price.text = "€$valueTruncated"
        holder.priceSmall.text = s.substring(1, s.length)

        holder.itemView.setOnClickListener {
            val price = itemList[holder.bindingAdapterPosition]
            coinPriceInterface.onClick(holder.bindingAdapterPosition, price)
        }
        holder.salePrice.text = Html.fromHtml("<strike>€${coinPrice.originalPrice}</strike>")
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    interface CoinPriceInterface {
        fun onClick(index: Int, coinPrice: CoinPrice)
    }
}