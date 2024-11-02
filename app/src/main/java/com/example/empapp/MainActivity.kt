package com.example.empapp

import AlphaVantageApi
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private lateinit var percentageChangeText: TextView
    private lateinit var chart: Chart
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
        val dataFetcher = DataFetcher(api, apiKey)

        dataFetcher.getStockData("AAPL") { entries ->
            chart.setUpLineChartData(entries)
        }
    }
}
