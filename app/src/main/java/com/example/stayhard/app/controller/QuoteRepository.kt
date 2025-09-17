package com.example.stayhard.app.controller

import com.example.stayhard.app.model.Quote
import com.example.stayhard.app.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuoteRepository(private val apiService: ApiService) {
    suspend fun getRandomQuote(): Quote {
        return withContext(Dispatchers.IO) {
            apiService.getRandomQuote()
        }
    }
}