package com.example.empapp

import AlphaVantageApi
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private lateinit var percentageChangeText: TextView
    private lateinit var chart: Chart
    private lateinit var dataFetcher: DataFetcher
    private val apiKey = "BEK571R5O9F75ZNI"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lineChart = findViewById(R.id.lineChart)
        percentageChangeText = findViewById(R.id.percentageChangeText)

        chart = Chart(lineChart, percentageChangeText)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.alphavantage.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(AlphaVantageApi::class.java)
        dataFetcher = DataFetcher(api, apiKey)

        val stock = "AAPL"

        dataFetcher.getStockData(stock) { data ->
            val entries = data.toMutableList()

            CoroutineScope(Dispatchers.Main).launch {
                val currentPrice = dataFetcher.fetchCurrentPrice(stock)
                if (currentPrice != null) {
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    entries.add(Pair(today, Entry(entries.size.toFloat(), currentPrice)))
                }

                chart.setUpLineChartData(entries)
                lineChart.invalidate()
            }
        }
    }
}
