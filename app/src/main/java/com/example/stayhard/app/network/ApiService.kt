package com.example.stayhard.app.network

import com.example.stayhard.app.model.Quote
import retrofit2.http.GET

interface ApiService {
    @GET("citation.php")
    suspend fun getRandomQuote(): Quote
}