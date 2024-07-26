package com.ali.alipool.view.market

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ali.alipool.viewModel.MyViewModel
import com.ali.alipool.model.CoinsData
import com.ali.alipool.networkChecker.NetworkChecker
import com.ali.alipool.view.coin.CoinActivity
import com.example.alipool.databinding.ActivityMarketBinding
import ir.dunijet.dunipool.apiManager.model.CoinAboutItem


class MarketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMarketBinding
    private lateinit var viewModel: MyViewModel
    lateinit var aboutDataMap: MutableMap<String, CoinAboutItem>
    private lateinit var adapter: MarketAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.layoutToolbar.toolbar.title = "Market"

        viewModel = ViewModelProvider(this)[MyViewModel::class.java]

        aboutDataMap = viewModel.getAboutDataFromAssets(this)



        viewModel.dataNews.observe(this@MarketActivity) { data ->
            refreshNews(data)
        }
        viewModel.dataCoins.observe(this@MarketActivity) { data ->
            showDataInRecycler(data)
        }



        binding.swipeRefreshMain.setOnRefreshListener {
            getDataFromVM()
        // viewModel.refreshData()
        }


        binding.layoutWatchlist.btnShowMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.livecoinwatch.com/"))
            startActivity(intent)
        }


    }

    private fun getDataFromVM() {
        if (NetworkChecker(this).isInternetConnect)
            viewModel.refreshData()
        else {
            Toast.makeText(this, "your connection is fail", Toast.LENGTH_SHORT).show()
            binding.swipeRefreshMain.isRefreshing = false
        }

    }


    private fun refreshNews(data: ArrayList<Pair<String, String>>) {
        val randomAccess = (0..49).random()
        binding.layoutNews.txtNews.text = data[randomAccess].first
        binding.layoutNews.imgNews.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data[randomAccess].second))
            startActivity(intent)
        }
        binding.layoutNews.txtNews.setOnClickListener {
            refreshNews(data)
        }
    }

    private fun showDataInRecycler(data: List<CoinsData.Data>) {
        adapter = MarketAdapter(ArrayList(data), object : MarketAdapter.RecyclerCallback {
            override fun onCoinItemClicked(dataCoin: CoinsData.Data) {
                val intent = Intent(this@MarketActivity, CoinActivity::class.java)

                val bundle = Bundle()
                bundle.putParcelable("bundle1", dataCoin)
                bundle.putParcelable("bundle2", aboutDataMap[dataCoin.coinInfo.name])

                intent.putExtra("bundle", bundle)
                startActivity(intent)
            }
        })
        binding.layoutWatchlist.recyclerMain.adapter = adapter
        binding.layoutWatchlist.recyclerMain.layoutManager = LinearLayoutManager(this)
        binding.swipeRefreshMain.isRefreshing = false
    }


    override fun onResume() {
        super.onResume()
        binding.swipeRefreshMain.isRefreshing = true
//        viewModel.refreshData()
        getDataFromVM()
    }

}