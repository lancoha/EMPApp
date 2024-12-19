package com.example.empapp

import TwelveDataApi
import TimeSeriesResponse
import android.util.Log
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataFetcher(
    private val api: TwelveDataApi,
    private val apiKey: String
) {

    suspend fun fetchCurrentPrice(stock: String): Float? {
        if (stock.contains("/")) {
            return null
        }
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://finance.yahoo.com/quote/$stock"
                val doc = Jsoup.connect(url).get()
                val priceText = doc.select("fin-streamer[data-field=regularMarketPrice]").first()?.text()
                priceText?.toFloatOrNull()
            } catch (e: Exception) {
                Log.e("DataFetcher", "Error fetching current price", e)
                null
            }
        }
    }

    fun getStockData(
        symbol: String,
        onDataFetched: (List<Pair<String, Entry>>) -> Unit
    ) {
        val startDate = "2000-01-01"
        val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        api.getTimeSeries(
            symbol = symbol,
            interval = "1day",
            startDate = startDate,
            endDate = endDate,
            apiKey = apiKey
        ).enqueue(object : Callback<TimeSeriesResponse> {
            override fun onResponse(call: Call<TimeSeriesResponse>, response: Response<TimeSeriesResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.status == "ok") {
                        val entries = body.values.reversed().mapIndexed { index, value ->
                            try {
                                val date = value.datetime
                                val closePrice = value.close.toFloat()
                                Pair(date, Entry(index.toFloat(), closePrice))
                            } catch (e: NumberFormatException) {
                                Log.e("Parsing Error", "Error parsing close value: ${value.close}", e)
                                null
                            }
                        }.filterNotNull()

                        if (entries.isNotEmpty()) {
                            onDataFetched(entries)
                        } else {
                            Log.e("DataFetcher", "No valid entries to display on the chart")
                            onDataFetched(emptyList())
                        }
                    } else {
                        val message = body?.message ?: "Unknown error"
                        Log.e("DataFetcher", "API Error: Status=${body?.status}, Message=$message")
                        onDataFetched(emptyList())
                    }
                } else {
                    Log.e("DataFetcher", "HTTP Error: Code=${response.code()}, Message=${response.message()}")
                    onDataFetched(emptyList())
                }
            }

            override fun onFailure(call: Call<TimeSeriesResponse>, t: Throwable) {
                Log.e("DataFetcher", "Network or API Failure", t)
                onDataFetched(emptyList())
            }
        })
    }
}
