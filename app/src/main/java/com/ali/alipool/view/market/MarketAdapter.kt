package com.ali.alipool.view.market

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ali.alipool.model.BASE_URL_IMAGE
import com.ali.alipool.model.CoinsData
import com.example.alipool.R
import com.example.alipool.databinding.ItemRecyclerMarketBinding

class MarketAdapter(
    private var data: ArrayList<CoinsData.Data>,
    private val recyclerCallback: RecyclerCallback
) :
    RecyclerView.Adapter<MarketAdapter.MarketViewHolder>() {
    lateinit var binding: ItemRecyclerMarketBinding

    inner class MarketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindViews(dataCoin: CoinsData.Data) {
            if (dataCoin != null && dataCoin.rAW != null) {
                binding.txtCoinName.text = dataCoin.coinInfo.fullName

                binding.txtPrice.text = dataCoin.dISPLAY.uSD.pRICE

                val change = dataCoin.rAW.uSD.cHANGEPCT24HOUR
                if (change > 0) {
                    binding.txtTaghir.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.colorGain
                        )
                    )
                    binding.txtTaghir.text = dataCoin.dISPLAY.uSD.cHANGEPCT24HOUR + " %"
                } else if (change < 0) {
                    binding.txtTaghir.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.colorLoss
                        )
                    )
                    binding.txtTaghir.text = dataCoin.dISPLAY.uSD.cHANGEPCT24HOUR + " %"
                } else {
                    binding.txtTaghir.text = "0 %"
                }

                binding.txtMarketCap.text = dataCoin.dISPLAY.uSD.mKTCAP

                Glide
                    .with(itemView)
                    .load(BASE_URL_IMAGE + dataCoin.coinInfo.imageUrl)
                    .into(binding.imgItem)


                itemView.setOnClickListener {
                    recyclerCallback.onCoinItemClicked(dataCoin)
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemRecyclerMarketBinding.inflate(inflater, parent, false)

        return MarketViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        holder.bindViews(data[position])
    }

    override fun getItemCount(): Int = data.size

    interface RecyclerCallback {
        fun onCoinItemClicked(dataCoin: CoinsData.Data)
    }

}