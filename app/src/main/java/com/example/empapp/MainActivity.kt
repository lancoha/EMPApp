// MainActivity.kt
package com.example.empapp

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class MainActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the LineChart view in the layout
        lineChart = findViewById(R.id.lineChart)

        // Set up data for the chart
        setUpLineChartData()
    }

    private fun setUpLineChartData() {
        // Create a list of data points (entries) for the chart
        val entries = listOf(
            Entry(0f, 1f),
            Entry(1f, 2f),
            Entry(2f, 4f),
            Entry(3f, 3f),
            Entry(4f, 5f),
            Entry(5f, 7f),
            Entry(6f, 8f),
            Entry(7f, 6f),
            Entry(8f, 8f),
            Entry(9f, 5f),
            Entry(10f, 4f),
            Entry(11f, 3.213f),
            Entry(12f, 12f),
            Entry(13f, 9f),
            Entry(15f, 20f),
            Entry(16f, 19f),
            Entry(17f, 17f),
            Entry(18f, 20.5f),
            Entry(19f, 20f),
            Entry(20f, 25f),
            Entry(21f, 26f),
        )

        // Create a dataset and set its properties
        val dataSet = LineDataSet(entries, "Sample Data").apply {
            color = Color.BLUE
            lineWidth = 2f
            setCircleColor(Color.RED)
            circleRadius = 5f
            valueTextColor = Color.BLACK
            setDrawFilled(true)
            fillColor = Color.CYAN
        }

        // Create line data with the dataset
        val lineData = LineData(dataSet)

        // Set data to the chart and refresh it
        lineChart.data = lineData
        lineChart.invalidate() // Refresh chart with data
    }
}
