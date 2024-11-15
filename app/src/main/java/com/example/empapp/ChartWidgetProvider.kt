package com.example.empapp

import TwelveDataApi
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
            .baseUrl("https://api.twelvedata.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(TwelveDataApi::class.java)
        val apiKey = "849555d1edc54fabb611c9c13c62c0ea"
        val dataFetcher = DataFetcher(api, apiKey)

        for (appWidgetId in appWidgetIds) {
            val stockSymbol = loadSymbolPref(context, appWidgetId) ?: "GOOG"
            updateAppWidget(context, appWidgetManager, appWidgetId, stockSymbol, dataFetcher)
        }
    }

    companion object {
        private const val PREFS_NAME = "com.example.empapp.ChartWidgetProvider"
        private const val PREF_PREFIX_KEY = "appwidget_symbol_"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            stockSymbol: String,
            dataFetcher: DataFetcher
        ) {
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setOnClickPendingIntent(R.id.widget_chart_image, pendingIntent)

            val lineChart = LineChart(context)
            val textView = TextView(context)

            dataFetcher.getStockData(stockSymbol) { data ->
                val dataList = data.toMutableList()
                CoroutineScope(Dispatchers.Main).launch {
                    val currentPrice = dataFetcher.fetchCurrentPrice(stockSymbol)

                    if (currentPrice != null) {
                        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val lastDateInData = dataList.lastOrNull()?.first
                        if (lastDateInData != today) {
                            dataList.add(Pair(today, Entry(dataList.size.toFloat(), currentPrice)))
                        }
                    }

                    val chart = Chart(lineChart, textView)
                    chart.setUpLineChartForWidget(dataList)
                    val chartBitmap: Bitmap = chart.getChartBitmap(800, 200)

                    val entries = dataList.map { it.second }
                    val change1D = if (entries.size > 1) {
                        val lastValue = entries[entries.size - 1].y
                        val previousValue = entries[entries.size - 2].y
                        if (previousValue != 0f) ((lastValue - previousValue) / previousValue) * 100 else 0f
                    } else {
                        0f
                    }
                    val formattedChange1D = String.format("%.2f", change1D)

                    val change7D = chart.calculateChangePercentage(entries, dataList, 7)
                    val change30D = chart.calculateChangePercentage(entries, dataList, 30)
                    val price = entries.lastOrNull()?.y ?: 0f
                    views.setTextViewText(R.id.stock, stockSymbol)
                    views.setTextViewText(R.id.current_price, "$price")
                    views.setTextViewText(R.id.change_1d, "1D: $formattedChange1D%")
                    views.setTextViewText(R.id.change_7d, "7D: $change7D%")
                    views.setTextViewText(R.id.change_30d, "30D: $change30D%")
                    views.setImageViewBitmap(R.id.widget_chart_image, chartBitmap)

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }

        fun loadSymbolPref(context: Context, appWidgetId: Int): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
        }
    }
}
