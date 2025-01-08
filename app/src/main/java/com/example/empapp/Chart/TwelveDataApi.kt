package com.example.empapp.Chart

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TwelveDataApi {
    @GET("time_series")
    fun getTimeSeries(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String = "1day",
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?,
        @Query("apikey") apiKey: String
    ): Call<TimeSeriesResponse>
}
