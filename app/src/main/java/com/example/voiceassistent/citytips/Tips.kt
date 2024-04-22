package com.example.voiceassistent.citytips


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


// Data class for individual city information
class CityTip : Serializable {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null
    // ... other fields if needed ...
}

// Class to hold the entire response with the city map
class CityTipsListResponse : Serializable {
    var cityMap: Map<String, CityTip>? = null
}