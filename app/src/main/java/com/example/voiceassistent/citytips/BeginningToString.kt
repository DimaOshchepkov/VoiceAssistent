package com.example.voiceassistent.citytips

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.function.Consumer

class BeginningToString {

    fun getCityInformation(beginning: String?, callback: Consumer<List<String?>?>) {
        val api: TipsApi? = TipsService().getApi()
        val call: Call<Map<String, CityTip>?>? = api?.getListTipsCity(beginning)

        call!!.enqueue(object : Callback<Map<String, CityTip>?> {
            override fun onResponse(call: Call<Map<String, CityTip>?>, response: Response<Map<String, CityTip>?>) {
                val result = response.body()
                if (result != null) {
                    val answer: List<String?> = result.values
                        .take(3) // Take the first 3 cities
                        .map { it.name } // Extract city names

                    callback.accept(answer)
                } else {
                    callback.accept(null)
                }
            }

            override fun onFailure(call: Call<Map<String, CityTip>?>, t: Throwable) {
                Log.w("CITY", t.message.toString())
            }
        })
    }
}