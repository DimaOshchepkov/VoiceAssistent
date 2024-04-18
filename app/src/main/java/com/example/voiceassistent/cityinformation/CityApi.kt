package com.example.voiceassistent.cityinformation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CityApi {
    @GET("xml/geo/search?api_key=7e1da54d21f2b5e9f4203a4cfad75af9")
    fun getCurrentCity(@Query("search") city: String?) : Call<CityInformation?>?
}