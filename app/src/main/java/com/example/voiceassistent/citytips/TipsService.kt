package com.example.voiceassistent.citytips

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TipsService {
    fun getApi(): TipsApi? {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://htmlweb.ru/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(TipsApi::class.java)
    }
}