package com.example.empapp

import AlphaVantageApi
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setOnClickPendingIntent(R.id.widget_chart_image, pendingIntent)

            val lineChart = LineChart(context)
            val textView = TextView(context)

            dataFetcher.getStockData("AAPL") { data ->
                val chart = Chart(lineChart, textView)
                chart.setUpLineChartForWidget(data)

                val chartBitmap: Bitmap = chart.getChartBitmap(400, 400)

                views.setImageViewBitmap(R.id.widget_chart_image, chartBitmap)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
