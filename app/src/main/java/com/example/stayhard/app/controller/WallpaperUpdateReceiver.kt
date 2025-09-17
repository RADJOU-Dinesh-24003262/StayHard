package com.example.stayhard.app.controller

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.stayhard.app.utils.ImageGenerator
import com.example.stayhard.app.network.ApiServiceFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WallpaperUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Créer le repository directement
        val repository = QuoteRepository(ApiServiceFactory.create())

        // Utiliser coroutine
        GlobalScope.launch {
            try {
                val quote = repository.getRandomQuote()
                val bitmap = ImageGenerator.generateQuoteImage(context, quote)
                val wallpaperManager = WallpaperManager.getInstance(context)
                wallpaperManager.setBitmap(bitmap)
            } catch (e: Exception) {
                Log.e("WallpaperUpdate", "Erreur: ${e.message}")
            }
        }
    }
}