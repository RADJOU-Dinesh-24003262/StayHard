package com.example.stayhard.app.utils

import android.graphics.Bitmap

/**
 * Gestionnaire de Bitmap pour éviter les memory leaks
 */
class BitmapManager {
    private var currentBitmap: Bitmap? = null

    /**
     * Remplace le bitmap actuel par un nouveau
     * Recycle automatiquement l'ancien pour libérer la mémoire
     */
    @Synchronized
    fun updateBitmap(newBitmap: Bitmap?) {
        try {
            // ✅ Recycler l'ancien bitmap avant de le remplacer
            currentBitmap?.let { oldBitmap ->
                if (!oldBitmap.isRecycled) {
                    oldBitmap.recycle()
                }
            }
            currentBitmap = newBitmap
        } catch (e: Exception) {
            android.util.Log.e("BitmapManager", "Erreur updateBitmap: ${e.message}")
        }
    }

    /**
     * Récupère le bitmap actuel
     */
    fun getCurrentBitmap(): Bitmap? = currentBitmap

    /**
     * Nettoie les ressources (à appeler dans onDestroy)
     */
    fun clear() {
        currentBitmap?.let { bitmap ->
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
        currentBitmap = null
    }

    /**
     * Vérifie si un bitmap est disponible
     */
    fun hasBitmap(): Boolean = currentBitmap != null && !currentBitmap!!.isRecycled
}