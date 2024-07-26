package com.ali.alipool.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ali.alipool.model.ALL
import com.ali.alipool.model.ApiService
import com.ali.alipool.model.BASE_URL
import com.ali.alipool.model.ChartData
import com.ali.alipool.model.CoinsData
import com.ali.alipool.model.HISTO_DAY
import com.ali.alipool.model.HISTO_HOUR
import com.ali.alipool.model.HISTO_MINUTE
import com.ali.alipool.model.HOUR
import com.ali.alipool.model.HOURS24
import com.ali.alipool.model.MONTH
import com.ali.alipool.model.MONTH3
import com.ali.alipool.model.WEEK
import com.ali.alipool.model.YEAR
import com.google.gson.Gson
import ir.dunijet.dunipool.apiManager.model.CoinAboutItem
import ir.dunijet.dunipool.apiManager.model.CoinsAboutData
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MyViewModel : ViewModel() {

    private val _dataNews = MutableLiveData<ArrayList<Pair<String, String>>>()
    val dataNews: LiveData<ArrayList<Pair<String, String>>> = _dataNews

    private val _dataCoins = MutableLiveData<List<CoinsData.Data>>()
    val dataCoins: LiveData<List<CoinsData.Data>> = _dataCoins

    private val retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiService = retrofit.create(ApiService::class.java)


    fun refreshData() {
        getNews()
        getCoinList()
    }

    //coroutineScope and LiveData
    private fun getNews() {
        viewModelScope.launch {
            try {
                val response = apiService.getTopNews()
                val dataToSend: ArrayList<Pair<String, String>> = arrayListOf()
                response.data.forEach {
                    dataToSend.add(Pair(it.title, it.url))
                }
                _dataNews.value = dataToSend
            } catch (e: Exception) {
                Log.d("dataNews", "getNew1: ${e.message}")
            }
        }

    }


    private fun getCoinList() {
        try {
            viewModelScope.launch {
                val response = apiService.getTopCoins()
                _dataCoins.value = cleanData(response.data)
                Log.d("dataCoins", "getCoinList: ${response.data}")
            }
        } catch (e: Exception) {
            Log.d("dataCoins", "getNew1: ${e.message}")
        }

    }

    private fun cleanData(data: List<CoinsData.Data>): List<CoinsData.Data> {
        val newData = mutableListOf<CoinsData.Data>()

        data.forEach {
            if (it.rAW != null && it.dISPLAY != null)
                newData.add(it)
        }
        return newData
    }

    //Call and CallBack
    fun getChartData(
        symbol: String,
        period: String,
        apiCallback: ApiCallback<Pair<List<ChartData.Data>, ChartData.Data?>>
    ) {
        var histoPeriod = ""
        var limit = 30
        var aggregate = 1
        when (period) {
            HOUR -> {
                histoPeriod = HISTO_MINUTE
                limit = 60
                aggregate = 12
            }

            HOURS24 -> {
                histoPeriod = HISTO_HOUR
                limit = 24
            }

            WEEK -> {
                histoPeriod = HISTO_HOUR
                aggregate = 6
            }

            MONTH -> {
                histoPeriod = HISTO_DAY
                limit = 30
            }

            MONTH3 -> {
                histoPeriod = HISTO_DAY
                limit = 90
            }

            YEAR -> {
                histoPeriod = HISTO_DAY
                aggregate = 13
            }

            ALL -> {
                histoPeriod = HISTO_DAY
                aggregate = 30
                limit = 2000
            }
        }

        apiService.getChartData(histoPeriod, symbol, limit, aggregate)
            .enqueue(object : Callback<ChartData> {
                override fun onResponse(call: Call<ChartData>, response: Response<ChartData>) {

                    val data1 = response.body()!!.data
                    val data2 = response.body()!!.data.maxByOrNull { it.close.toFloat() }
                    val returningData = Pair(data1, data2)

                    apiCallback.onSuccess(returningData)
                }

                override fun onFailure(call: Call<ChartData>, t: Throwable) {
                    apiCallback.onError(t.message!!)
                }
            })

    }


    fun getAboutDataFromAssets(context: Context): MutableMap<String, CoinAboutItem> {
        val fileInString = context.assets
            .open("currencyinfo.json")
            .bufferedReader()
            .use { it.readText() }

        val aboutDataMap = mutableMapOf<String, CoinAboutItem>()

        val dataAboutAll = Gson().fromJson(fileInString, CoinsAboutData::class.java)

        dataAboutAll.forEach {
            aboutDataMap[it.currencyName] = CoinAboutItem(
                it.info.web,
                it.info.github,
                it.info.twt,
                it.info.desc,
                it.info.reddit
            )
        }
        return aboutDataMap
    }


    interface ApiCallback<T> {
        fun onSuccess(data: T)
        fun onError(errorMessage: String)
    }

}

