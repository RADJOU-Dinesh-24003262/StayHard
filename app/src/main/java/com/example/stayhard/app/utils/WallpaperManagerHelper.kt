package com.example.stayhard.app.utils

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log

/**
 * Helper pour gérer les opérations sur le fond d'écran
 * Évite la duplication de code
 */
object WallpaperManagerHelper {
    
    /**
     * Définit un bitmap comme fond d'écran de verrouillage
     * Gère automatiquement les différentes versions d'Android
     * 
     * @param context Contexte Android
     * @param bitmap Image à définir comme fond d'écran
     * @return true si succès, false sinon
     */
    fun setLockScreenWallpaper(context: Context, bitmap: Bitmap): Boolean {
        return try {
            val wallpaperManager = WallpaperManager.getInstance(context)

            // 24+ : Définir uniquement pour l'écran de verrouillage
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(
                    bitmap, 
                    null, 
                    true, 
                    WallpaperManager.FLAG_LOCK
                )
            } else {
                // Pour les versions plus anciennes, définir comme fond d'écran principal
                wallpaperManager.setBitmap(bitmap)
            }

            Log.d(Constants.LogTags.WALLPAPER_UPDATE, "Fond d'écran défini avec succès")
            true

        } catch (e: SecurityException) {
            Log.e(Constants.LogTags.WALLPAPER_UPDATE, "Permission refusée: ${e.message}", e)
            false
        } catch (e: Exception) {
            Log.e(Constants.LogTags.WALLPAPER_UPDATE, "Erreur lors de la définition du fond d'écran: ${e.message}", e)
            false
        }
    }
    
    /**
     * Définit un bitmap comme fond d'écran principal (home screen)
     * 
     * @param context Contexte Android
     * @param bitmap Image à définir comme fond d'écran
     * @return true si succès, false sinon
     */
    fun setHomeScreenWallpaper(context: Context, bitmap: Bitmap): Boolean {
        return try {
            val wallpaperManager = WallpaperManager.getInstance(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(
                    bitmap, 
                    null, 
                    true, 
                    WallpaperManager.FLAG_SYSTEM
                )
            } else {
                wallpaperManager.setBitmap(bitmap)
            }

            Log.d(Constants.LogTags.WALLPAPER_UPDATE, "Fond d'écran principal défini avec succès")
            true

        } catch (e: Exception) {
            Log.e(Constants.LogTags.WALLPAPER_UPDATE, "Erreur: ${e.message}", e)
            false
        }
    }
    
    /**
     * Définit un bitmap pour les deux écrans (home + lock)
     */
    fun setBothScreensWallpaper(context: Context, bitmap: Bitmap): Boolean {
        val lockSuccess = setLockScreenWallpaper(context, bitmap)
        val homeSuccess = setHomeScreenWallpaper(context, bitmap)
        return lockSuccess && homeSuccess
    }
}