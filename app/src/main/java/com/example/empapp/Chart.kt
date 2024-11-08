package com.example.empapp

import android.graphics.Color
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Chart(private val lineChart: LineChart, private val percentageChangeText: TextView) {

    fun setUpLineChartData(data: List<Pair<String, Entry>>) {
        val entries = data.map { it.second }

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

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        val dateFormat = SimpleDateFormat("d.M.yyyy", Locale.getDefault())

        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < data.size) {
                    val date = data[index].first
                    dateFormat.format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)!!)
                } else ""
            }
        }

        val markerView = MyMarkerView(lineChart.context, R.layout.marker_layout, data)
        lineChart.marker = markerView
        lineChart.setTouchEnabled(true)
        lineChart.isHighlightPerTapEnabled = true

        lineChart.invalidate()

        val change30Days = calculateChangePercentage(entries, data, 30)
        val change7Days = calculateChangePercentage(entries, data, 7)
        val change1Day = calculateChangePercentage(entries, data, 1)

        val percentageChange = calculatePercentageChange(firstValue, lastValue)
        val currentPrice = entries.lastOrNull()?.y ?: 0f
        percentageChangeText.text = "Price: $currentPrice\nChange: $percentageChange%\n\n" +
                "30 dni: $change30Days%\n" +
                "7 dni: $change7Days%\n" +
                "1 dan: $change1Day%"
    }

    private fun calculatePercentageChange(firstValue: Float, lastValue: Float): String {
        return if (firstValue != 0f) {
            val change = ((lastValue - firstValue) / firstValue) * 100
            String.format("%.2f", change)
        } else {
            "N/A"
        }
    }


    private fun calculateChangePercentage(entries: List<Entry>, data: List<Pair<String, Entry>>, days: Int): String {
        if (entries.isEmpty()) {
            return "N/A"
        }

        val currentEntry = entries.last()
        val currentDate = data.last().first

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currDate = dateFormat.parse(currentDate) ?: return "N/A"
        calendar.time = currDate
        calendar.add(Calendar.DAY_OF_MONTH, -days) // Subtract the number of days

        val pastDate = dateFormat.format(calendar.time)

        val pastEntryIndex = data.indexOfFirst { it.first == pastDate }
        if (pastEntryIndex == -1) {
            return "N/A"
        }

        val pastEntry = entries[pastEntryIndex]
        val pastValue = pastEntry.y
        val currentValue = currentEntry.y

        val change = if (pastValue != 0f) ((currentValue - pastValue) / pastValue) * 100 else 0f
        val formattedChange = String.format("%.2f", change)

        return formattedChange
    }

}
