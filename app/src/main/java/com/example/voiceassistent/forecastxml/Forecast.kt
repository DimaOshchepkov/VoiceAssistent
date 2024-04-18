package com.example.voiceassistent.forecastxml


import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "current", strict = false)
class ForecastXml {
    @field:Element(name="temperature")
    var temperature: TemperatureXml? = null

    @field:Element(name="weather")
    var weather: WeatherXml? = null
}
@Root(name = "temperature", strict = false)
class TemperatureXml {
    @field:Attribute(name = "value")
    var value: String? = null
}
@Root(name = "weather", strict = false)
class WeatherXml  {
    @field:Attribute(name = "value")
    var value: String? = null
}