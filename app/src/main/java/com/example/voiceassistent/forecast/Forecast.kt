package com.example.voiceassistent.forecast

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Forecast : Serializable {
    @SerializedName("main")
    @Expose
    var temperature: Temperature? = null

    @SerializedName("weather")
    @Expose
    var weather: Weather? = null
}

class Temperature {
    @SerializedName("value")
    @Expose
    var value: String? = null
}

class Weather {
    @SerializedName("description")
    @Expose
    var value: String? = null
}