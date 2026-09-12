package com.example.stayhard.app.controller

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.stayhard.app.utils.ImageGenerator
import com.example.stayhard.app.utils.Constants
import kotlinx.coroutines.*

/**
 * Receiver pour les alarmes de mise à jour automatique
 * CORRECTION: Gestion propre du scope de coroutine
 */
class WallpaperUpdateReceiver : BroadcastReceiver() {

    // Variable pour suivre le job
    private var updateJob: Job? = null

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(Constants.LogTags.WALLPAPER_RECEIVER, "Alarme reçue à : ${System.currentTimeMillis()}")

        // Utiliser goAsync() pour opérations longues
        val pendingResult = goAsync()

        // Créer un scope avec SupervisorJob (crash isolé)
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        // Stocker le job pour pouvoir l'annuler si nécessaire
        updateJob = scope.launch {
            try {
                // Utiliser Dependencies au lieu de créer directement
                val repository = com.example.stayhard.app.di.Dependencies.provideQuoteRepository()

                // Récupérer la citation
                val quote = repository.getRandomQuote()

                // Générer l'image
                val bitmap = ImageGenerator.generateQuoteImage(context, quote)

                try {
                    // Définir comme fond d'écran de verrouillage
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                    } else {
                        wallpaperManager.setBitmap(bitmap)
                    }

                    Log.d(Constants.LogTags.WALLPAPER_RECEIVER, "Fond d'écran mis à jour automatiquement")
                } finally {
                    // Recycler le bitmap après utilisation
                    if (!bitmap.isRecycled) {
                        bitmap.recycle()
                    }
                }

            } catch (e: Exception) {
                Log.e(Constants.LogTags.WALLPAPER_RECEIVER, "Erreur lors de la mise à jour automatique: ${e.message}", e)
            } finally {
                // Signaler la fin du traitement
                pendingResult.finish()
                // Nettoyer la référence du job
                updateJob = null
            }
        }
    }

    /**
     * Méthode pour annuler le job si nécessaire
     */
    fun cancelUpdate() {
        updateJob?.cancel()
        updateJob = null
    }
}