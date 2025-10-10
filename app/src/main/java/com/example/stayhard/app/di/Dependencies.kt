package com.example.stayhard.app.di

import com.example.stayhard.app.controller.QuoteRepository
import com.example.stayhard.app.network.ApiService
import com.example.stayhard.app.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * ✅ Service Locator simple (alternative à Dagger/Hilt)
 * Centralise la création des dépendances
 */
object Dependencies {

    // ✅ Lazy initialization : créé seulement quand nécessaire
    private val apiService: ApiService by lazy {
        createRetrofit().create(ApiService::class.java)
    }

    private val repository: QuoteRepository by lazy {
        QuoteRepository(apiService)
    }

    /**
     * Fournit l'instance du Repository
     * Point d'accès unique pour toute l'app
     */
    fun provideQuoteRepository(): QuoteRepository = repository

    /**
     * Fournit l'ApiService (utile pour les tests)
     */
    fun provideApiService(): ApiService = apiService

    /**
     * Crée l'instance Retrofit
     * Configuration centralisée
     */
    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.Api.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Pour les tests : permet d'injecter un mock
     */
    fun setTestRepository(testRepo: QuoteRepository) {
        // Implémentation future pour les tests unitaires
    }
}