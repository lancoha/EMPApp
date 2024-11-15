package com.example.empapp

import TwelveDataApi
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChartWidgetConfigureActivity : Activity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var symbolInput: EditText
    private lateinit var dataFetcher: DataFetcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chart_widget_configure)

        setResult(RESULT_CANCELED)

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

        symbolInput = findViewById(R.id.symbol_input)

        findViewById<Button>(R.id.add_button).setOnClickListener {
            val symbol = symbolInput.text.toString().trim().uppercase()

            if (symbol.isEmpty()) {
                symbolInput.error = "Please enter a valid symbol"
                return@setOnClickListener
            }

            saveSymbolPref(this@ChartWidgetConfigureActivity, appWidgetId, symbol)

            val appWidgetManager = AppWidgetManager.getInstance(this@ChartWidgetConfigureActivity)

            ChartWidgetProvider.updateAppWidget(
                this@ChartWidgetConfigureActivity,
                appWidgetManager,
                appWidgetId,
                symbol,
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

        fun saveSymbolPref(context: Context, appWidgetId: Int, symbol: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            prefs.putString(PREF_PREFIX_KEY + appWidgetId, symbol)
            prefs.apply()
        }
    }
}
