package com.example.voiceassistent.forecastxml

import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class ForecastServiceXml {
    fun getApi(): ForecastApiXml? {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org") //Базовая часть адреса
            .addConverterFactory(SimpleXmlConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
        .build()
        return retrofit.create(ForecastApiXml::class.java) //Создание объекта, при помощи которого будут выполняться запросы
    }

}