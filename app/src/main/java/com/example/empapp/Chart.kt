package com.example.empapp

import android.graphics.Color
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class Chart(private val lineChart: LineChart, private val percentageChangeText: TextView) {

    fun setUpLineChartData() {
        val entries = listOf(
            Entry(0f, 1.5f),
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
            Entry(21f, 24f),
            Entry(22f, 12f),
            Entry(23f, 9f),
            Entry(24f, 20f),
            Entry(25f, 19f),
            Entry(26f, 17f),
            Entry(27f, 20.5f),
            Entry(28f, 20f),
            Entry(29f, 25f),
            Entry(30f, 28f)


        )

        val firstValue = entries.first().y
        val lastValue = entries.last().y
        val lineColor = when {
            firstValue > lastValue -> Color.RED
            firstValue < lastValue -> Color.GREEN
            else -> Color.BLUE
        }

        val dataSet = LineDataSet(entries, "Name").apply {
            color = lineColor
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
            valueTextColor = Color.BLACK
            setDrawFilled(false)
        }

        //hide grid
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.axisRight.setDrawGridLines(false)

        //hide labels
        lineChart.xAxis.setDrawLabels(false)
        lineChart.axisLeft.setDrawLabels(false)
        lineChart.axisRight.setDrawLabels(false)
        lineChart.description.isEnabled = false

        //hide borders
        lineChart.setDrawBorders(false) // ne dela? vvvvv
        lineChart.xAxis.setDrawAxisLine(false) // Disable the X-axis line
        lineChart.axisLeft.setDrawAxisLine(false) // Disable the left Y-axis line
        lineChart.axisRight.setDrawAxisLine(false) // Disable the right Y-axis line


        val lineData = LineData(dataSet)

        lineChart.data = lineData
        lineChart.invalidate()

        val percentageChange = calculatePercentageChange(firstValue, lastValue)
        percentageChangeText.text = "Change: $percentageChange%"

    }
    private fun calculatePercentageChange(firstValue: Float, lastValue: Float): String {
        return if (firstValue != 0f) {
            val change = ((lastValue - firstValue) / firstValue) * 100
            String.format("%.2f", change)
        } else {
            "N/A"
        }
    }
}
