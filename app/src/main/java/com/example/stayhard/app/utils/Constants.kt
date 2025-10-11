package com.example.stayhard.app.utils

/**
 * Constantes globales de l'application
 */
object Constants {

    // ✅ Configuration de l'image
    object Image {
        const val WIDTH = 1080
        const val HEIGHT = 1920
        const val MARGIN = 200f
        const val TEXT_SIZE_MULTIPLIER = 2.5f
        const val AUTHOR_SIZE_RATIO = 0.7f
        const val SHADOW_OFFSET = 4f
        const val SHADOW_ALPHA = 100
        const val AUTHOR_SPACING = 80f
    }

    // ✅ Valeurs par défaut du style
    object DefaultStyle {
        const val FONT_FAMILY = "Arial"
        const val FONT_SIZE = 24
        const val TEXT_COLOR = "#FFFFFF"
        const val BACKGROUND_COLOR = "#667eea"
        const val SHAPE = "rectangle"
        const val POSITION = "center"
    }

    // ✅ API - HTTPS OBLIGATOIRE
    object Api {
        // 🔒 CHANGÉ: HTTP -> HTTPS pour la sécurité
        const val BASE_URL = "https://citation.dineshradj.eu/"
        const val ENDPOINT = "/api"
    }

    // ✅ SharedPreferences
    object Prefs {
        const val NAME = "stayhard_prefs"
        const val KEY_ALARMS_SCHEDULED = "alarms_scheduled"
    }

    // ✅ Logs
    object LogTags {
        const val WALLPAPER_UPDATE = "WallpaperUpdate"
        const val WALLPAPER_RECEIVER = "WallpaperReceiver"
        const val QUOTE_VIEWMODEL = "QuoteViewModel"
        const val ALARM_MANAGER = "AlarmManager"
        const val IMAGE_GENERATOR = "ImageGenerator"
        const val PERMISSION = "Permission"
    }

    // ✅ Messages utilisateur
    object Messages {
        const val WALLPAPER_UPDATED = "Fond d'écran mis à jour"
        const val WALLPAPER_ERROR = "Erreur lors de la mise à jour du fond d'écran"
        const val QUOTE_UPDATED = "Citation mise à jour !"
        const val QUOTE_ERROR = "Erreur lors du chargement"
        const val PERMISSION_DENIED = "Permission refusée"
    }

    // ✅ Codes de requête pour permissions
    object RequestCodes {
        const val SET_WALLPAPER = 100
    }
}