package com.example.voiceassistent.forecast

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.function.Consumer

class ForecastToString {
    fun getForecast(city: String?, callback: Consumer<String>) {
        val api: ForecastApi? = ForecastService().getApi()
        val call: Call<Forecast?>? = api?.getCurrentWeather(city)

        call!!.enqueue(object : Callback<Forecast?> {
            override fun onResponse(call: Call<Forecast?>?, response: Response<Forecast?>?) {
                val result = response?.body()
                if (result != null) {
                    val answer =
                        "Сейчас где-то ${result.main?.temp} ${getCorrectString(result.main?.temp.toString())} и ${result.weather[0]?.description}"
                    callback.accept(answer)
                } else {
                    callback.accept("Не могу узнать погоду")
                }
            }
            override fun onFailure(call: Call<Forecast?>, t: Throwable) {
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