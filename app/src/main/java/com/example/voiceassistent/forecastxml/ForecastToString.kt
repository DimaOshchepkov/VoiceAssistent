package com.example.voiceassistent.forecastxml

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.function.Consumer

class ForecastToStringXml {
    fun getForecast(city: String?, callback: Consumer<String>) {
        val api: ForecastApiXml? = ForecastServiceXml().getApi()
        val call: Call<ForecastXml?>? = api?.getCurrentWeather(city)

        call!!.enqueue(object : Callback<ForecastXml?> {
            override fun onResponse(call: Call<ForecastXml?>?, response: Response<ForecastXml?>?) {
                val result = response?.body()
                if (result != null) {
                    val answer =
                        "Сейчас где-то ${result.temperature?.value} ${getCorrectString(result.temperature?.value.toString())} и ${result.weather?.value}"
                    callback.accept(answer)
                } else {
                    callback.accept("Не могу узнать погоду")
                }
            }
            override fun onFailure(call: Call<ForecastXml?>, t: Throwable) {
                Log.w("WEATHER", t.message.toString())
            }
        })
    }

    fun getCorrectString(temp: String?): String {
        return when {
            temp == null -> "градусов"
            "." in temp -> "градуса"
            temp.lastOrNull()?.digitToInt() == 1 -> "градус"
            temp.lastOrNull()?.digitToInt() in 2..4 -> "градуса"
            else -> "градусов"
        }
    }
}