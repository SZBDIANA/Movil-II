package com.example.microproyecto

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object WeatherApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
    private const val API_KEY = "bbe12144b51e34e664061066b8230c66"

    suspend fun getWeather(city: String): WeatherResponse {
        val response: HttpResponse = client.get(BASE_URL) {
            parameter("q", city)
            parameter("appid", API_KEY)
            parameter("units", "metric")
        }
        return response.body()
    }
}

@Serializable
data class WeatherResponse(
    val name: String,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val clouds: Clouds,
    val rain: Rain? = null,
    val snow: Snow? = null,
    val sys: Sys,
    val coord: Coord,
    val dt: Long,
    val timezone: Int
)

@Serializable data class Weather(val main: String, val description: String, val icon: String)
@Serializable data class Main(val temp: Float, val feels_like: Float, val temp_min: Float, val temp_max: Float, val pressure: Int, val humidity: Int)
@Serializable data class Wind(val speed: Float, val deg: Int, val gust: Float? = null)
@Serializable data class Clouds(val all: Int)
@Serializable data class Rain(val `1h`: Float? = null, val `3h`: Float? = null)
@Serializable data class Snow(val `1h`: Float? = null, val `3h`: Float? = null)
@Serializable data class Sys(val country: String, val sunrise: Long, val sunset: Long)
@Serializable data class Coord(val lon: Float, val lat: Float)
