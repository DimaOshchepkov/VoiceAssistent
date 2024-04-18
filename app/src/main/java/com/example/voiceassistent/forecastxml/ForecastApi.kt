package com.example.voiceassistent.forecastxml

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApiXml {
    @GET("/data/2.5/weather?appid=d059638fdace48545a25e4b15f0942ae&lang=ru&units=metric&mode=xml")
    fun getCurrentWeather(@Query("q") city: String?): Call<ForecastXml?>?
}