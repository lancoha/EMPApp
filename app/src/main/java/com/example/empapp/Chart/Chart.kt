package com.example.empapp.Chart

import android.graphics.Color
import android.util.Log
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import com.example.empapp.MainActivity
import com.example.empapp.R

class Chart(private val lineChart: LineChart, private val percentageChangeText: TextView) {

    fun filterDataForTimeFrame(data: List<Pair<String, Entry>>, timeFrame: String): List<Pair<String, Entry>> {
        if (timeFrame == "ALL") return data

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        when (timeFrame) {
            "1M" -> calendar.add(Calendar.MONTH, -1)
            "1Y" -> calendar.add(Calendar.YEAR, -1)
            "5Y" -> calendar.add(Calendar.YEAR, -5)
        }

        val cutoffDate = dateFormat.format(calendar.time)
        val filteredData = data.filter { it.first >= cutoffDate }

        return filteredData.mapIndexed { index, pair ->
            Pair(pair.first, Entry(index.toFloat(), pair.second.y))
        }
    }

    fun updateChartWithTimeFrame(data: List<Pair<String, Entry>>, timeFrame: String) {
        val filteredData = filterDataForTimeFrame(data, timeFrame)
        setUpLineChartData(filteredData)

        lineChart.fitScreen()
        lineChart.moveViewToX(0f)
        lineChart.highlightValues(null)
    }

    fun updateChartWithTimeFrameWidget(data: List<Pair<String, Entry>>, timeFrame: String) {
        val filteredData = filterDataForTimeFrame(data, timeFrame)
        setUpLineChartForWidget(filteredData)

        lineChart.fitScreen()
        lineChart.moveViewToX(0f)
        lineChart.highlightValues(null)
    }

    fun setUpLineChartData(data: List<Pair<String, Entry>>) {
        val entries = data.map { it.second }

        val firstValue = entries.firstOrNull()?.y ?: 0f
        val lastValue = entries.lastOrNull()?.y ?: 0f
        val currentPrice = entries.lastOrNull()?.y ?: 0f
        val lineColor = when {
            firstValue > lastValue -> Color.RED
            firstValue < lastValue -> Color.GREEN
            else -> Color.BLUE
        }

        val dataSet = LineDataSet(entries, MainActivity.GlobalVariables.ChartSymbol).apply {
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

        val change1MDays = calculateChangePercentage(entries, data, 31)
        val change7Days = calculateChangePercentage(entries, data, 7)
        val change1Day = calculateChangePercentage(entries, data, 1)

        val percentageChange = calculatePercentageChange(firstValue, lastValue)

        percentageChangeText.text = "Price: $$currentPrice\nChange: $percentageChange%\n\n" +
                "1 mesec: $change1MDays%\n" +
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

    fun calculateChangePercentage(entries: List<Entry>, data: List<Pair<String, Entry>>, days: Int): String {
        if (entries.isEmpty()) {
            return "N/A"
        }

        val currentEntry = entries.last()
        val currentDate = data.last().first

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currDate = dateFormat.parse(currentDate) ?: return "N/A"
        calendar.time = currDate

        if (days == 31) {
            calendar.add(Calendar.MONTH, -1)
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, -days)
        }

        val pastDate = dateFormat.format(calendar.time)

        val pastEntryIndex = data.indexOfLast { it.first <= pastDate }
        if (pastEntryIndex == -1) {
            return "N/A"
        }

        val pastEntry = entries[pastEntryIndex]
        val pastValue = pastEntry.y
        val currentValue = currentEntry.y

        val change = if (pastValue != 0f) ((currentValue - pastValue) / pastValue) * 100 else 0f
        val formattedChange = String.format("%.2f", change)

        Log.d("Chart", "Interval: $days days, Past Date: $pastDate, Past Value: $pastValue, " +
                "Current Date: $currentDate, Current Value: $currentValue, Change: $formattedChange%")

        return formattedChange
    }
    fun getChartBitmap(width: Int = 400, height: Int = 400): Bitmap {
        lineChart.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        )
        lineChart.layout(0, 0, width, height)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        lineChart.draw(canvas)
        return bitmap
    }

    fun setUpLineChartForWidget(data: List<Pair<String, Entry>>) {
        val entries = data.map { it.second }

        val firstValue = entries.firstOrNull()?.y ?: 0f
        val lastValue = entries.lastOrNull()?.y ?: 0f
        val lineColor = when {
            firstValue > lastValue -> Color.RED
            firstValue < lastValue -> Color.GREEN
            else -> Color.BLUE
        }

        val dataSet = LineDataSet(entries, "").apply {
            color = lineColor
            lineWidth = 1f
            setDrawCircles(false)
            setDrawValues(false)
            valueTextColor = Color.BLACK
            setDrawFilled(false)
        }

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        lineChart.axisLeft.isEnabled = false
        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.description.isEnabled = false

        lineChart.xAxis.setDrawGridLines(false)
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.axisRight.setDrawGridLines(false)

        lineChart.setViewPortOffsets(-40f, 0f, 20f, 0f)
        lineChart.setExtraOffsets(0f, 0f, 0f, 0f)
        lineChart.minOffset = 0f
        lineChart.setPadding(0, 0, 0, 0)

        lineChart.setTouchEnabled(false)
        lineChart.setPinchZoom(false)
        lineChart.isDoubleTapToZoomEnabled = false

        lineChart.invalidate()
    }
}
