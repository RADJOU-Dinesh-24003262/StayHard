package com.example.stayhard.app.controller

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.stayhard.app.utils.ImageGenerator
import com.example.stayhard.app.network.ApiServiceFactory
import kotlinx.coroutines.*

class WallpaperUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WallpaperUpdate", "Alarme reçue à : ${System.currentTimeMillis()}")

        // ✅ CORRECTION : Utiliser goAsync() pour opérations longues
        val pendingResult = goAsync()

        // ✅ Créer un scope avec SupervisorJob (crash isolé)
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        scope.launch {
            try {
                // ✅ Utiliser Dependencies au lieu de créer directement
                val repository = com.example.stayhard.app.di.Dependencies.provideQuoteRepository()

                // Récupérer la citation
                val quote = repository.getRandomQuote()

                // Générer l'image
                val bitmap = ImageGenerator.generateQuoteImage(context, quote)

                // Définir comme fond d'écran de verrouillage
                val wallpaperManager = WallpaperManager.getInstance(context)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                } else {
                    wallpaperManager.setBitmap(bitmap)
                }

                Log.d("WallpaperReceiver", "Fond d'écran mis à jour automatiquement")

            } catch (e: Exception) {
                Log.e("WallpaperReceiver", "Erreur lors de la mise à jour automatique: ${e.message}", e)
            } finally {
                // ✅ IMPORTANT : Signaler la fin du traitement
                pendingResult.finish()
            }
        }
    }
}