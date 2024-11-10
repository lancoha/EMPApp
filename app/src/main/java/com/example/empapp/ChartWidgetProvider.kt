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
        // Set up Retrofit instance for API calls
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.alphavantage.co")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(AlphaVantageApi::class.java)

        // Initialize DataFetcher with API and key
        val apiKey = "your_api_key" // Replace with your actual API key
        val dataFetcher = DataFetcher(api, apiKey)

        // Iterate over each widget instance
        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setOnClickPendingIntent(R.id.widget_chart_image, pendingIntent)

            // Initialize LineChart and TextView for Chart class
            val lineChart = LineChart(context)
            val textView = TextView(context)

            // Fetch stock data asynchronously
            dataFetcher.getStockData("AAPL") { data ->
                // Create and set up Chart with widget-specific configuration
                val chart = Chart(lineChart, textView)
                chart.setUpLineChartForWidget(data) // Use the widget-specific setup method

                // Generate the chart bitmap and update the widget's ImageView
                val chartBitmap: Bitmap = chart.getChartBitmap(400, 400) // Passing size directly

                // Set the generated bitmap to the widget's ImageView
                views.setImageViewBitmap(R.id.widget_chart_image, chartBitmap)

                // Update the widget with new data
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
