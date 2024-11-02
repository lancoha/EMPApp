package com.example.empapp

import android.graphics.Color
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class Chart(private val lineChart: LineChart, private val percentageChangeText: TextView) {

    fun setUpLineChartData(entries: List<Entry>) {
        val firstValue = entries.firstOrNull()?.y ?: 0f
        val lastValue = entries.lastOrNull()?.y ?: 0f
        val lineColor = when {
            firstValue > lastValue -> Color.RED
            firstValue < lastValue -> Color.GREEN
            else -> Color.BLUE
        }


        val dataSet = LineDataSet(entries, "AAPL").apply {
            color = lineColor
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
            valueTextColor = Color.BLACK
            setDrawFilled(false)
        }


        //hide
        /*
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
        lineChart.xAxis.setDrawAxisLine(false)
        lineChart.axisLeft.setDrawAxisLine(false)
        lineChart.axisRight.setDrawAxisLine(false)
        */

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
