package com.example.voiceassistent.citytips

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TipsApi {
    @GET("geo/api.php?json&api_key=7e1da54d21f2b5e9f4203a4cfad75af9")
    fun getListTipsCity(@Query("city_name") city: String?) : Call<Map<String, CityTip>?>?
}