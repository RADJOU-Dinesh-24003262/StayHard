package com.example.stayhard.app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceFactory {
    fun create(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.29:8080/") // Remplacez par votre URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            
        return retrofit.create(ApiService::class.java)
    }
}