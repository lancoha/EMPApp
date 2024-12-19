package com.example.empapp

import TwelveDataApi
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChartsScreen : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private lateinit var percentageChangeText: TextView
    private lateinit var chart: Chart
    private lateinit var dataFetcher: DataFetcher
    private val apiKey = "849555d1edc54fabb611c9c13c62c0ea"

    private val viewModel: AssetViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = AssetRepository.getInstance(applicationContext)
                return AssetViewModel(repo) as T
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lineChart = findViewById(R.id.lineChart)
        percentageChangeText = findViewById(R.id.percentageChangeText)

        chart = Chart(lineChart, percentageChangeText)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.twelvedata.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(TwelveDataApi::class.java)
        dataFetcher = DataFetcher(api, apiKey)


        dataFetcher.getStockData(MainActivity.GlobalVariables.ChartSymbol) { data ->
            lifecycleScope.launch {
                if (data.isNotEmpty()) {
                    handleApiData(MainActivity.GlobalVariables.ChartSymbol, data)
                } else {
                    fetchDataFromDatabase(MainActivity.GlobalVariables.ChartSymbol)
                }
            }
        }
    }

    private fun handleApiData(stock: String, data: List<Pair<String, Entry>>) {
        lifecycleScope.launch {
            val fullData = data.toMutableList()

            val currentPrice = dataFetcher.fetchCurrentPrice(stock)
            if (currentPrice != null) {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                fullData.add(Pair(today, Entry(fullData.size.toFloat(), currentPrice)))
            }

            chart.setUpLineChartData(fullData)

            processAssetBeforeSave(stock, fullData)

            setupChartButtons(fullData)
        }
    }

    private suspend fun processAssetBeforeSave(symbol: String, entries: List<Pair<String, Entry>>) {
        val repo = AssetRepository.getInstance(applicationContext)

        val existingAsset = repo.getAllAssets().first().find { it.id == symbol }

        if (existingAsset != null) {
            if (existingAsset.isFavourite) {
                saveDataToDatabase(symbol, true, entries)
            }
        } else {
            viewModel.addNewAsset(symbol, false)
        }
    }

    private fun fetchDataFromDatabase(stock: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val repo = AssetRepository.getInstance(applicationContext)

            val dailyData = repo.getDailyDataForAsset(stock).first()

            if (dailyData.isNotEmpty()) {
                val entries = dailyData.mapIndexed { index, data ->
                    Pair(data.datetime, Entry(index.toFloat(), data.close.toFloat()))
                }

                launch(Dispatchers.Main) {
                    chart.setUpLineChartData(entries)
                    setupChartButtons(entries)
                }
            } else {
                launch(Dispatchers.Main) {
                    percentageChangeText.text = "V PB ni podatkov za prikaz.(Asset ni pod Favourites)"
                }
            }
        }
    }

    private fun setupChartButtons(data: List<Pair<String, Entry>>) {
        findViewById<Button>(R.id.btn_1M).setOnClickListener {
            chart.updateChartWithTimeFrame(data, "1M")
        }

        findViewById<Button>(R.id.btn_1Y).setOnClickListener {
            chart.updateChartWithTimeFrame(data, "1Y")
        }

        findViewById<Button>(R.id.btn_5Y).setOnClickListener {
            chart.updateChartWithTimeFrame(data, "5Y")
        }

        findViewById<Button>(R.id.btn_all).setOnClickListener {
            chart.updateChartWithTimeFrame(data, "ALL")
        }

        findViewById<Button>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }

    private fun saveDataToDatabase(symbol: String, isFavourite: Boolean, entries: List<Pair<String, Entry>>) {
        lifecycleScope.launch {
            val repo = AssetRepository.getInstance(applicationContext)

            if (isFavourite) {
                val dailyDataList = entries.map { (date, entry) ->
                    AssetDailyData(
                        assetId = symbol,
                        datetime = date,
                        close = entry.y.toDouble()
                    )
                }

                viewModel.addNewAsset(symbol, true)

                if (dailyDataList.isNotEmpty()) {
                    viewModel.addDailyDataForAsset(dailyDataList)
                }
            } else {
                val currentAssets = repo.getAllAssets()
                val assetList = currentAssets.stateIn(this).value
                val assetExists = assetList.any { it.id == symbol }

                if (!assetExists) {
                    viewModel.addNewAsset(symbol, false)
                }

                repo.updateFavouriteStatus(symbol, false)
            }
        }
    }
}
