package com.example.voiceassistent.forecast

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ForecastService {
    fun getApi(): ForecastApi? {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org") //Базовая часть адреса
            .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
        .build()
        return retrofit.create(ForecastApi::class.java) //Создание объекта, при помощи которого будут выполняться запросы
    }

}