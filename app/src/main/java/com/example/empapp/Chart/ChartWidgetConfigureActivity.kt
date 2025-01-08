package com.example.empapp.Chart

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.example.empapp.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChartWidgetConfigureActivity : Activity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var symbolInput: EditText
    private lateinit var timeframeSpinner: Spinner
    private lateinit var dataFetcher: DataFetcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chart_widget_configure)

        setResult(RESULT_CANCELED)

        symbolInput = findViewById(R.id.symbol_input)
        timeframeSpinner = findViewById(R.id.timeframe_spinner)

        val timeframes = listOf("ALL", "5Y", "1Y", "1M")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, timeframes)
        timeframeSpinner.adapter = adapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.twelvedata.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(TwelveDataApi::class.java)
        val apiKey = "849555d1edc54fabb611c9c13c62c0ea"
        dataFetcher = DataFetcher(api, apiKey)

        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        findViewById<Button>(R.id.add_button).setOnClickListener {
            val symbol = symbolInput.text.toString().trim().uppercase()
            val selectedTimeframe = timeframeSpinner.selectedItem.toString()

            if (symbol.isEmpty()) {
                symbolInput.error = "Please enter a valid symbol"
                return@setOnClickListener
            }

            saveSymbolPref(this, appWidgetId, symbol)
            saveTimeframePref(this, appWidgetId, selectedTimeframe)

            val appWidgetManager = AppWidgetManager.getInstance(this)
            ChartWidgetProvider.updateAppWidget(
                this,
                appWidgetManager,
                appWidgetId,
                symbol,
                selectedTimeframe,
                dataFetcher
            )

            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }

    companion object {
        private const val PREFS_NAME = "com.example.empapp.ChartWidgetProvider"
        private const val PREF_PREFIX_KEY = "appwidget_symbol_"
        private const val PREF_TIMEFRAME_KEY = "appwidget_timeframe_"

        fun saveSymbolPref(context: Context, appWidgetId: Int, symbol: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            prefs.putString(PREF_PREFIX_KEY + appWidgetId, symbol)
            prefs.apply()
        }

        fun saveTimeframePref(context: Context, appWidgetId: Int, timeframe: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            prefs.putString(PREF_TIMEFRAME_KEY + appWidgetId, timeframe)
            prefs.apply()
        }

        fun loadTimeframePref(context: Context, appWidgetId: Int): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(PREF_TIMEFRAME_KEY + appWidgetId, "ALL")
        }
    }
}
