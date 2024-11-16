package com.example.empapp

import TwelveDataApi
import android.os.Bundle
import android.widget.Button
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
    private val apiKey = "849555d1edc54fabb611c9c13c62c0ea"

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

        val stock = "AAPL"

        dataFetcher.getStockData(stock) { data ->
            val fullData = data.toMutableList()

            CoroutineScope(Dispatchers.Main).launch {
                val currentPrice = dataFetcher.fetchCurrentPrice(stock)
                if (currentPrice != null) {
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    fullData.add(Pair(today, Entry(fullData.size.toFloat(), currentPrice)))
                }

                chart.setUpLineChartData(fullData)

                findViewById<Button>(R.id.btn_1M).setOnClickListener {
                    chart.updateChartWithTimeFrame(fullData, "1M")
                }

                findViewById<Button>(R.id.btn_1Y).setOnClickListener {
                    chart.updateChartWithTimeFrame(fullData, "1Y")
                }

                findViewById<Button>(R.id.btn_5Y).setOnClickListener {
                    chart.updateChartWithTimeFrame(fullData, "5Y")
                }

                findViewById<Button>(R.id.btn_all).setOnClickListener {
                    chart.updateChartWithTimeFrame(fullData, "ALL")
                }
            }
        }
    }
}
