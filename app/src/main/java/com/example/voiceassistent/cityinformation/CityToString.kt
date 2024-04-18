package com.example.voiceassistent.cityinformation

import java.util.function.Consumer

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CityToString {
    fun getCityInformaion(city: String?, callback: Consumer<String?>) {
        val api: CityApi? = CityService().getApi()
        val call: Call<CityInformation?>? = api?.getCurrentCity(city)

        call!!.enqueue(object : Callback<CityInformation?> {
            override fun onResponse(call: Call<CityInformation?>, response: Response<CityInformation?>) {
                val result = response.body()
                if (result != null) {
                    val answer: String = result.city?.cityMsgs
                        ?.take(3)
                        ?.joinToString(separator = "\n\n") { buildCityInfoString(it) } // Объединить строки с разделителем "\n\n"
                        ?.let { "Вот, что я нашел:\n\n$it" } // Добавить префикс "Вот, что я нашел:"
                        ?: ""
                    callback.accept(answer)
                } else {
                    callback.accept("Не получается узнать информацию о городе")
                }
            }

            override fun onFailure(call: Call<CityInformation?>, t: Throwable) {
                Log.w("CITY", t.message.toString())
            }
        })
    }

    fun buildCityInfoString(town: Town): String {
        return """
        |Полное название: ${town.fullName}
        |Больше информации на сайте: ${town.url}
    """.trimMargin()
    }

}