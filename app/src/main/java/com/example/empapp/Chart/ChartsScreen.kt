package com.example.empapp.Chart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.empapp.Database.AssetDailyData
import com.example.empapp.Database.AssetRepository
import com.example.empapp.Database.AssetViewModel
import com.example.empapp.MainActivity
import com.example.empapp.R
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

    private lateinit var toggleFavButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lineChart = findViewById(R.id.lineChart)
        percentageChangeText = findViewById(R.id.percentageChangeText)
        toggleFavButton = findViewById(R.id.btn_toggle_fav)

        chart = Chart(lineChart, percentageChangeText)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.twelvedata.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(TwelveDataApi::class.java)
        dataFetcher = DataFetcher(api, apiKey)
        lineChart.setNoDataText("Fetching data from API...")
        lineChart.invalidate()
        dataFetcher.getStockData(MainActivity.GlobalVariables.ChartSymbol) { data ->
            lifecycleScope.launch {
                if (data.isNotEmpty()) {
                    lineChart.setNoDataText("Processing API data...")
                    lineChart.invalidate()
                    handleApiData(MainActivity.GlobalVariables.ChartSymbol, data)
                } else {
                    lineChart.setNoDataText("Failed to fetch API data. Loading data from the database...")
                    lineChart.invalidate()
                    fetchDataFromDatabase(MainActivity.GlobalVariables.ChartSymbol)
                }
            }
        }

        findViewById<Button>(R.id.btn_main_screen).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
            lineChart.setNoDataText("Displaying the chart...")
            lineChart.invalidate()
            chart.setUpLineChartData(fullData)
            lineChart.invalidate()

            processAssetBeforeSave(stock, fullData)
            setupChartButtons(fullData)
        }
    }

    private suspend fun processAssetBeforeSave(symbol: String, entries: List<Pair<String, Entry>>) {
        val repo = AssetRepository.getInstance(applicationContext)
        val existingAsset = repo.getAllAssets().first().find { it.id == symbol }

        if (existingAsset != null) {
            setupToggleFavButton(symbol, existingAsset.isFavourite, entries)
            if (existingAsset.isFavourite) {
                saveDataToDatabase(symbol, true, entries)
            }
        } else {
            viewModel.addNewAsset(symbol, false)
            setupToggleFavButton(symbol, false, entries)
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

    private fun setupToggleFavButton(
        symbol: String,
        isFavourite: Boolean,
        currentEntries: List<Pair<String, Entry>>
    ) {
        toggleFavButton.text = if (isFavourite) "Unfavourite" else "Favourite"

        toggleFavButton.setOnClickListener {
            val newFav = !isFavourite

            lifecycleScope.launch {
                val repo = AssetRepository.getInstance(applicationContext)

                repo.updateFavouriteStatus(symbol, newFav)

                if (newFav) {
                    viewModel.addNewAsset(symbol, true)

                    val dailyDataList = currentEntries.map { (date, entry) ->
                        AssetDailyData(
                            assetId = symbol,
                            datetime = date,
                            close = entry.y.toDouble()
                        )
                    }
                    if (dailyDataList.isNotEmpty()) {
                        viewModel.addDailyDataForAsset(dailyDataList)
                    }
                }
                setupToggleFavButton(symbol, newFav, currentEntries)
            }
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
                    lineChart.setNoDataText("Displaying the chart...")
                    lineChart.invalidate()
                    chart.setUpLineChartData(entries)
                    lineChart.invalidate()
                    setupChartButtons(entries)

                    val existingAsset = repo.getAllAssets().first().find { it.id == stock }
                    if (existingAsset != null) {
                        setupToggleFavButton(stock, existingAsset.isFavourite, entries)
                    } else {
                        viewModel.addNewAsset(stock, false)
                        setupToggleFavButton(stock, false, entries)
                    }
                }
            } else {
                launch(Dispatchers.Main) {
                    lineChart.setNoDataText("No data to display from the database")
                    lineChart.invalidate()
                    percentageChangeText.text = "Favourite the asset for data to be stored in the database"

                    val existingAsset = repo.getAllAssets().first().find { it.id == stock }
                    if (existingAsset != null) {
                        setupToggleFavButton(stock, existingAsset.isFavourite, emptyList())
                    } else {
                        viewModel.addNewAsset(stock, false)
                        setupToggleFavButton(stock, false, emptyList())
                    }
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
    }
}