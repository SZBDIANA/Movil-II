package com.example.exchangeRate.network

import retrofit2.http.GET

interface ExchangeRateApiService {

    @GET("v6/c11c403c6ecfaf798939bc28/latest/MXN")
    suspend fun getLatestExchangeRates(): ExchangeRateResponse
}