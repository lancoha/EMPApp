package com.example.empapp

import TwelveDataApi
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.widget.RemoteViews
import androidx.work.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

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
            val stockSymbol = loadSymbolPref(context, appWidgetId) ?: ""
            val timeframe = ChartWidgetConfigureActivity.loadTimeframePref(context, appWidgetId) ?: "ALL"
            updateAppWidget(context, appWidgetManager, appWidgetId, stockSymbol, timeframe, dataFetcher)
        }
    }

    override fun onEnabled(context: Context) {
        scheduleWidgetUpdate(context)
    }

    override fun onDisabled(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("WidgetUpdateWork")
    }

    companion object {
        private const val PREFS_NAME = "com.example.empapp.ChartWidgetProvider"
        private const val PREF_PREFIX_KEY = "appwidget_symbol_"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            stockSymbol: String,
            timeframe: String,
            dataFetcher: DataFetcher
        ) {
            val updateIntent = Intent(context, ChartWidgetProvider::class.java).apply {
                action = "com.example.empapp.UPDATE_WIDGET"
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val updatePendingIntent = PendingIntent.getBroadcast(
                context, appWidgetId, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setOnClickPendingIntent(R.id.widget_chart_image, updatePendingIntent)

            val lineChart = LineChart(context)
            val textView = android.widget.TextView(context)

            dataFetcher.getStockData(stockSymbol) { data ->
                CoroutineScope(Dispatchers.Main).launch {
                    if (data != null && data.isNotEmpty()) {
                        try {
                            val dataList = data.toMutableList()
                            val currentPrice = dataFetcher.fetchCurrentPrice(stockSymbol)

                            if (currentPrice != null) {
                                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                val lastDateInData = dataList.lastOrNull()?.first
                                if (lastDateInData != today) {
                                    dataList.add(Pair(today, Entry(dataList.size.toFloat(), currentPrice)))
                                }
                            }

                            val chart = Chart(lineChart, textView)
                            chart.updateChartWithTimeFrameWidget(dataList, timeframe)

                            val chartBitmap: Bitmap = chart.getChartBitmap(800, 200)
                            views.setImageViewBitmap(R.id.widget_chart_image, chartBitmap)

                            val entries = dataList.map { it.second }
                            val price = entries.lastOrNull()?.y ?: 0f
                            views.setTextViewText(R.id.timeframe, timeframe)
                            views.setTextViewText(R.id.stock, stockSymbol)
                            views.setTextViewText(R.id.current_price, "$$price")

                            val change1D = chart.calculateChangePercentage(entries, dataList, 1)
                            val change7D = chart.calculateChangePercentage(entries, dataList, 7)
                            val change1M = chart.calculateChangePercentage(entries, dataList, 31)

                            views.setTextViewText(R.id.change_1d, "1D: $change1D%")
                            views.setTextViewText(R.id.change_7d, "7D: $change7D%")
                            views.setTextViewText(R.id.change_30d, "1M: $change1M%")

                            CoroutineScope(Dispatchers.IO).launch {
                                val repo = AssetRepository.getInstance(context)
                                val isFavourite = repo.getAllAssets().first().any { it.id == stockSymbol && it.isFavourite }

                                if (isFavourite) {
                                    saveDataToDatabase(context, stockSymbol, dataList)
                                }
                            }

                            appWidgetManager.updateAppWidget(appWidgetId, views)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            fetchDataFromDatabase(context, appWidgetManager, appWidgetId, stockSymbol, timeframe)
                        }
                    } else {
                        fetchDataFromDatabase(context, appWidgetManager, appWidgetId, stockSymbol, timeframe)
                    }
                }
            }
        }

        private fun fetchDataFromDatabase(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            stockSymbol: String,
            timeframe: String
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                val repo = AssetRepository.getInstance(context)
                val dailyData = repo.getDailyDataForAsset(stockSymbol).first()

                if (dailyData.isNotEmpty()) {
                    val entries = dailyData.mapIndexed { index, data ->
                        Pair(data.datetime, Entry(index.toFloat(), data.close.toFloat()))
                    }

                    withContext(Dispatchers.Main) {
                        val lineChart = LineChart(context)
                        val textView = android.widget.TextView(context)
                        val chart = Chart(lineChart, textView)
                        chart.updateChartWithTimeFrameWidget(entries, timeframe)

                        val chartBitmap: Bitmap = chart.getChartBitmap(800, 200)
                        val views = RemoteViews(context.packageName, R.layout.widget_layout)
                        views.setImageViewBitmap(R.id.widget_chart_image, chartBitmap)

                        val price = entries.map { it.second.y }.lastOrNull() ?: 0f
                        views.setTextViewText(R.id.timeframe, "$timeframe <- DB")
                        views.setTextViewText(R.id.stock, stockSymbol)
                        views.setTextViewText(R.id.current_price, "$$price")

                        val change1D = chart.calculateChangePercentage(entries.map { it.second }, entries, 1)
                        val change7D = chart.calculateChangePercentage(entries.map { it.second }, entries, 7)
                        val change1M = chart.calculateChangePercentage(entries.map { it.second }, entries, 31)

                        views.setTextViewText(R.id.change_1d, "1D: $change1D%")
                        views.setTextViewText(R.id.change_7d, "7D: $change7D%")
                        views.setTextViewText(R.id.change_30d, "1M: $change1M%")

                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                } else {
                    withContext(Dispatchers.Main) {

                        val views = RemoteViews(context.packageName, R.layout.widget_layout)

                        val blankBitmap = Bitmap.createBitmap(800, 200, Bitmap.Config.ARGB_8888)
                        val blankCanvas = Canvas(blankBitmap)
                        blankCanvas.drawColor(Color.TRANSPARENT)

                        views.setImageViewBitmap(R.id.widget_chart_image, blankBitmap)
                        views.setTextViewText(R.id.timeframe, "No data")
                        views.setTextViewText(R.id.stock, stockSymbol)
                        views.setTextViewText(R.id.current_price, "in database")
                        views.setTextViewText(R.id.change_1d, "1D: N/A")
                        views.setTextViewText(R.id.change_7d, "7D: N/A")
                        views.setTextViewText(R.id.change_30d, "1M: N/A")

                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                }
            }
        }

        private suspend fun saveDataToDatabase(
            context: Context,
            stockSymbol: String,
            dataList: List<Pair<String, Entry>>
        ) {
            withContext(Dispatchers.IO) {
                val repo = AssetRepository.getInstance(context)
                val dailyDataList = dataList.map { (date, entry) ->
                    AssetDailyData(
                        assetId = stockSymbol,
                        datetime = date,
                        close = entry.y.toDouble()
                    )
                }
                val existingAsset = repo.getAllAssets().first().find { it.id == stockSymbol }

                if (existingAsset != null && existingAsset.isFavourite) {
                    if (dailyDataList.isNotEmpty()) {
                        repo.insertAllDailyData(dailyDataList)
                    }
                }
            }
        }

        fun loadSymbolPref(context: Context, appWidgetId: Int): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
        }

        private fun scheduleWidgetUpdate(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(1, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "WidgetUpdateWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == "com.example.empapp.UPDATE_WIDGET") {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val stockSymbol = loadSymbolPref(context, appWidgetId) ?: ""
                val timeframe = ChartWidgetConfigureActivity.loadTimeframePref(context, appWidgetId) ?: "ALL"

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.twelvedata.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val api = retrofit.create(TwelveDataApi::class.java)
                val apiKey = "849555d1edc54fabb611c9c13c62c0ea"
                val dataFetcher = DataFetcher(api, apiKey)

                updateAppWidget(context, appWidgetManager, appWidgetId, stockSymbol, timeframe, dataFetcher)
            }
        }
    }
}

class WidgetUpdateWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val componentName = ComponentName(applicationContext, ChartWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.twelvedata.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(TwelveDataApi::class.java)
        val apiKey = "849555d1edc54fabb611c9c13c62c0ea"
        val dataFetcher = DataFetcher(api, apiKey)

        for (appWidgetId in appWidgetIds) {
            val stockSymbol = ChartWidgetProvider.loadSymbolPref(applicationContext, appWidgetId) ?: ""
            val timeframe = ChartWidgetConfigureActivity.loadTimeframePref(applicationContext, appWidgetId) ?: "ALL"
            ChartWidgetProvider.updateAppWidget(applicationContext, appWidgetManager, appWidgetId, stockSymbol, timeframe, dataFetcher)
        }

        return Result.success()
    }
}
