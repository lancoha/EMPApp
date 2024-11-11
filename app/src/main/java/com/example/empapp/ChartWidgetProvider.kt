package com.example.empapp

import AlphaVantageApi
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.graphics.Bitmap
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChartWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.alphavantage.co")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(AlphaVantageApi::class.java)

        val apiKey = "your_api_key"
        val dataFetcher = DataFetcher(api, apiKey)

        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setOnClickPendingIntent(R.id.widget_chart_image, pendingIntent)

            val lineChart = LineChart(context)
            val textView = TextView(context)

            dataFetcher.getStockData("AAPL") { data ->
                val data = data.toMutableList()
                val entries = data.map { it.second }
                CoroutineScope(Dispatchers.Main).launch {
                    val currentPrice = dataFetcher.fetchCurrentPrice()
                    if (currentPrice != null) {
                        val today =
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        data.add(Pair(today, Entry(data.size.toFloat(), currentPrice)))
                    }
                    val chart = Chart(lineChart, textView)
                    chart.setUpLineChartForWidget(data)

                    val chartBitmap: Bitmap = chart.getChartBitmap(400, 200)

                    val change1D = chart.calculateChangePercentage(entries, data, 1)
                    val change7D = chart.calculateChangePercentage(entries, data, 7)
                    val change30D = chart.calculateChangePercentage(entries, data, 30)

                    views.setTextViewText(R.id.change_1d, "1D: $change1D%")
                    views.setTextViewText(R.id.change_7d, "7D: $change7D%")
                    views.setTextViewText(R.id.change_30d, "30D: $change30D%")

                    views.setImageViewBitmap(R.id.widget_chart_image, chartBitmap)

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }
}