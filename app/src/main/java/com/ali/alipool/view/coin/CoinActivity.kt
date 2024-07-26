package com.ali.alipool.view.coin

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.ali.alipool.model.ALL
import com.ali.alipool.model.BASE_URL_TWITTER
import com.ali.alipool.model.ChartData
import com.ali.alipool.model.CoinsData
import com.ali.alipool.model.HOUR
import com.ali.alipool.model.HOURS24
import com.ali.alipool.model.MONTH
import com.ali.alipool.model.MONTH3
import com.ali.alipool.model.WEEK
import com.ali.alipool.model.YEAR
import com.ali.alipool.viewModel.MyViewModel
import com.example.alipool.R
import com.example.alipool.databinding.ActivityCoinBinding
import ir.dunijet.dunipool.apiManager.model.CoinAboutItem
class CoinActivity : AppCompatActivity() {
    lateinit var binding: ActivityCoinBinding
    private lateinit var coinData: CoinsData.Data
    private var dataThisCoinAbout: CoinAboutItem? = null
    private val viewModel = MyViewModel()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fromIntent = intent.getBundleExtra("bundle")!!
        coinData = fromIntent.getParcelable("bundle1", CoinsData.Data::class.java)!!
        dataThisCoinAbout = fromIntent.getParcelable("bundle2", CoinAboutItem::class.java)

        binding.layoutToolbar.toolbar.title = coinData.coinInfo.fullName

        initUi()

    }

    private fun initUi() {
        initChartUi()
        initStatistaticsUi()
        initAboutUi()
    }

    private fun initAboutUi() {
        binding.layoutAbout.txtWebsite.text = dataThisCoinAbout?.coinWebsite
        binding.layoutAbout.txtGithub.text = dataThisCoinAbout?.coinGithub
        binding.layoutAbout.txtReddit.text = dataThisCoinAbout?.coinReddit
        binding.layoutAbout.txtTwitter.text = dataThisCoinAbout?.coinTwitter
        binding.layoutAbout.txtAboutCoin.text = dataThisCoinAbout?.coinDesc

        binding.layoutAbout.txtWebsite.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout?.coinWebsite!!)
        }
        binding.layoutAbout.txtGithub.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout?.coinGithub!!)
        }
        binding.layoutAbout.txtReddit.setOnClickListener {
            openWebsiteDataCoin(dataThisCoinAbout?.coinReddit!!)
        }
        binding.layoutAbout.txtTwitter.setOnClickListener {
            openWebsiteDataCoin(BASE_URL_TWITTER + dataThisCoinAbout?.coinTwitter!!)
        }
    }
    private fun openWebsiteDataCoin(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun initStatistaticsUi() {
        binding.layoutStatistics.tvOpenAmount.text = coinData.dISPLAY.uSD.oPEN24HOUR
        binding.layoutStatistics.tvTodaysHighAmount.text = coinData.dISPLAY.uSD.hIGH24HOUR
        binding.layoutStatistics.tvTodayLowAmount.text = coinData.dISPLAY.uSD.lOW24HOUR
        binding.layoutStatistics.tvChangeTodayAmount.text = coinData.dISPLAY.uSD.cHANGE24HOUR
        binding.layoutStatistics.tvAlgoritm.text = coinData.coinInfo.name
        binding.layoutStatistics.tvTotulVolume.text = coinData.dISPLAY.uSD.tOTALVOLUME24H
        binding.layoutStatistics.tvAvgMarketCapAmount.text = coinData.dISPLAY.uSD.mKTCAP
        if (coinData.dISPLAY.uSD.sUPPLY.length > 8) {

            binding.layoutStatistics.tvSupplyNumber.text =
                coinData.dISPLAY.uSD.sUPPLY.substring(
                    0,
                    coinData.dISPLAY.uSD.sUPPLY.length - 10
                ) + " M"
        } else {
            binding.layoutStatistics.tvSupplyNumber.text = coinData.dISPLAY.uSD.sUPPLY
        }
    }



    @SuppressLint("SetTextI18n")
    private fun initChartUi() {

        var period: String = HOUR
        requestAndShowChart(period)
        binding.layoutChart.chartRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_12h -> {
                    period = HOUR
                }

                R.id.radio_1d -> {
                    period = HOURS24
                }

                R.id.radio_1w -> {
                    period = WEEK
                }

                R.id.radio_1m -> {
                    period = MONTH
                }

                R.id.radio_3m -> {
                    period = MONTH3
                }

                R.id.radio_1y -> {
                    period = YEAR
                }

                R.id.radio_all -> {
                    period = ALL
                }
            }
            requestAndShowChart(period)
        }

        binding.layoutChart.txtChartPrice.text = coinData.dISPLAY.uSD.pRICE
        binding.layoutChart.txtChartChange1.text = coinData.dISPLAY.uSD.cHANGE24HOUR
        binding.layoutChart.txtChartChange2.text = coinData.dISPLAY.uSD.cHANGEPCT24HOUR + " %"
        binding.layoutChart.sparkviewMain.setLineColor(
            ContextCompat.getColor(
                binding.root.context,
                R.color.bubble_gum_pink
            )
        )

        val taghir = coinData.rAW.uSD.cHANGEPCT24HOUR
        if (taghir > 0) {

            binding.layoutChart.txtChartChange2.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorGain
                )
            )
            binding.layoutChart.txtChartUpdown.text = "▲"

            binding.layoutChart.txtChartUpdown.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorGain
                )
            )


        } else if (taghir < 0) {

            binding.layoutChart.txtChartChange2.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorLoss
                )
            )
            binding.layoutChart.txtChartUpdown.text = "▼"
            binding.layoutChart.txtChartUpdown.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorLoss
                )
            )


        }


        binding.layoutChart.sparkviewMain.setScrubListener {
            // show price kamel
            if (it == null) {
                binding.layoutChart.txtChartPrice.text = coinData.dISPLAY.uSD.pRICE
            } else {
                // show price this dot
                binding.layoutChart.txtChartPrice.text =
                    "$ " + (it as ChartData.Data).close.toString()
            }

        }

    }
    private fun requestAndShowChart(period: String) {
        binding.layoutChart.animation.isVisible = true
        viewModel.getChartData(
            coinData.coinInfo.name,
            period,
            object : MyViewModel.ApiCallback<Pair<List<ChartData.Data>, ChartData.Data?>> {
                override fun onSuccess(data: Pair<List<ChartData.Data>, ChartData.Data?>) {
                    val chartAdapter = ChartAdapter(data.first, data.second?.open.toString())
                    binding.layoutChart.sparkviewMain.adapter = chartAdapter
                    binding.layoutChart.animation.isVisible = false
                }

                override fun onError(errorMessage: String) {
                    Toast.makeText(
                        this@CoinActivity,
                        "error => $errorMessage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

    }


}