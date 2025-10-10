package com.example.stayhard.app.model

data class Quote(
    val texte: String,
    val auteur: String,
    val style: Style? = null
)

data class Style(
    val fontFamily: String? = "Arial",
    val fontSize: Int? = 24,
    val textColor: String? = "#FFFFFF",
    val backgroundColor: String? = "#000000",
    val backgroundGradient: BackgroundGradient? = null,
    val shape: String? = "rectangle", // "rectangle", "rounded", "circle"
    val position: String? = "center" // "center", "top-left", "bottom-right"
)

data class BackgroundGradient(
    val startColor: String,
    val endColor: String,
    val direction: String = "vertical" // "vertical", "horizontal", "diagonal"
)