package com.example.empapp

import AlphaVantageApi
import StockResponse
import android.util.Log
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataFetcher(
    private val api: AlphaVantageApi,
    private val apiKey: String
) {

    suspend fun fetchCurrentPrice(): Float? {
        return withContext(Dispatchers.IO) {
            try {
                val doc = Jsoup.connect("https://finance.yahoo.com/quote/AAPL").get()
                val priceText = doc.select("fin-streamer[data-field=regularMarketPrice]").first()?.text()

                Log.d("fetchCurrentPrice", "Pridobljena cena (v obliki besedila): $priceText")

                priceText?.toFloatOrNull()?.also {
                    Log.d("fetchCurrentPrice", "Pretvorjena cena (Float): $it")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }


    fun getStockData(symbol: String, onDataFetched: (List<Pair<String, Entry>>) -> Unit) {
        api.getStockData(symbol = symbol, apiKey = apiKey).enqueue(object : Callback<StockResponse> {
            override fun onResponse(call: Call<StockResponse>, response: Response<StockResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { stockResponse ->
                        val timeSeries = stockResponse.timeSeriesDaily
                        val entries = mutableListOf<Pair<String, Entry>>()

                        if (timeSeries == null || timeSeries.isEmpty()) {
                            Log.e("API Response", "No time series data available or API limit reached")
                            return
                        }

                        val reversedEntries = timeSeries.entries.reversed()
                        reversedEntries.take(365).forEachIndexed { index, entry ->
                            val dailyData = entry.value
                            val date = entry.key

                            dailyData?.close?.let { closeValue ->
                                try {
                                    val entry = Entry(index.toFloat(), closeValue.toFloat())
                                    entries.add(Pair(date, entry))
                                } catch (e: NumberFormatException) {
                                    Log.e("Parsing Error", "Error parsing close value: $closeValue", e)
                                }
                            }
                        }

                        if (entries.isNotEmpty()) {
                            onDataFetched(entries)
                        } else {
                            Log.e("API Response", "No valid entries to display on the chart")
                        }
                    } ?: run {
                        Log.e("API Error", "Response body is null")
                    }
                } else {
                    Log.e("API Error", "Response Code: ${response.code()}, Message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StockResponse>, t: Throwable) {
                Log.e("API Failure", "Error fetching stock data", t)
            }
        })
    }
}
