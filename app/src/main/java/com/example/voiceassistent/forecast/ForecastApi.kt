package com.example.voiceassistent.forecast

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApi {
    @GET("/data/2.5/weather?appid=d059638fdace48545a25e4b15f0942ae&lang=ru&units=metric")
    fun getCurrentWeather(@Query("q") city: String?): Call<Forecast?>?
}