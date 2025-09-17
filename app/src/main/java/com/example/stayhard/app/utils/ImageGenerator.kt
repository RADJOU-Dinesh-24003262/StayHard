package com.example.stayhard.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import com.example.stayhard.app.model.Quote
import com.example.stayhard.app.model.Style
import kotlin.math.min

object ImageGenerator {
    fun generateQuoteImage(context: Context, quote: Quote): Bitmap {
        val width = 1080
        val height = 1920
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Utiliser un style par défaut si null
        val defaultStyle = Style()
        val style = quote.style ?: defaultStyle

        // Appliquer le fond (uni ou dégradé) - SANS FORME
        applyBackground(canvas, style, width, height)

        // Dessiner SEULEMENT le texte, pas de forme de fond
        drawTextWithShadow(canvas, quote, style)

        return bitmap
    }

    private fun applyBackground(canvas: Canvas, style: Style, width: Int, height: Int) {
        // Debug: toujours appliquer un dégradé même s'il n'y en a pas dans le JSON
        val gradient = style.backgroundGradient

        if (gradient != null) {
            println("DEBUG: Applying gradient from ${gradient.startColor} to ${gradient.endColor}")
            val paint = Paint().apply {
                isAntiAlias = true
                val startColor = Color.parseColor(gradient.startColor)
                val endColor = Color.parseColor(gradient.endColor)

                val shader = when(gradient.direction) {
                    "horizontal" -> LinearGradient(
                        0f, 0f, width.toFloat(), 0f,
                        startColor, endColor, Shader.TileMode.CLAMP
                    )
                    "diagonal" -> LinearGradient(
                        0f, 0f, width.toFloat(), height.toFloat(),
                        startColor, endColor, Shader.TileMode.CLAMP
                    )
                    else -> LinearGradient( // vertical par défaut
                        0f, 0f, 0f, height.toFloat(),
                        startColor, endColor, Shader.TileMode.CLAMP
                    )
                }
                this.shader = shader
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        } else {
            println("DEBUG: No gradient found, using solid color")
            // Couleur unie si pas de dégradé - utiliser une couleur par défaut colorée
            val bgColor = Color.parseColor(style.backgroundColor ?: "#667eea")
            canvas.drawColor(bgColor)
        }
    }

    private fun drawTextWithShadow(canvas: Canvas, quote: Quote, style: Style) {
        val textPaint = Paint().apply {
            color = Color.parseColor(style.textColor ?: "#FFFFFF")
            textSize = (style.fontSize?.toFloat() ?: 24f) * 2.5f // Plus grand pour impact
            typeface = Typeface.create(style.fontFamily ?: "Arial", Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val shadowPaint = Paint().apply {
            color = Color.argb(100, 0, 0, 0) // Ombre légère
            textSize = textPaint.textSize
            typeface = textPaint.typeface
            textAlign = textPaint.textAlign
            isAntiAlias = true
        }

        val centerX = canvas.width / 2f
        val centerY = canvas.height / 2f

        // Diviser le texte en lignes si nécessaire
        val maxWidth = canvas.width - 200f // Marges
        val lines = wrapText(quote.texte, textPaint, maxWidth)

        val lineHeight = textPaint.descent() - textPaint.ascent()
        val totalTextHeight = lines.size * lineHeight
        val startY = centerY - (totalTextHeight / 2) + lineHeight

        // Dessiner chaque ligne avec ombre
        lines.forEachIndexed { index, line ->
            val y = startY + (index * lineHeight)

            // Dessiner l'ombre (légèrement décalée)
            canvas.drawText(line, centerX + 4f, y + 4f, shadowPaint)
            // Dessiner le texte principal
            canvas.drawText(line, centerX, y, textPaint)
        }

        // Dessiner l'auteur
        val authorPaint = Paint().apply {
            color = Color.parseColor(style.textColor ?: "#FFFFFF")
            textSize = textPaint.textSize * 0.7f
            typeface = Typeface.create(style.fontFamily ?: "Arial", Typeface.ITALIC)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val authorY = startY + (lines.size * lineHeight) + 80f
        canvas.drawText("— ${quote.auteur}", centerX + 2f, authorY + 2f, shadowPaint.apply {
            textSize = authorPaint.textSize
            typeface = authorPaint.typeface
        })
        canvas.drawText("— ${quote.auteur}", centerX, authorY, authorPaint)
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val testWidth = paint.measureText(testLine)

            if (testWidth > maxWidth && currentLine.isNotEmpty()) {
                lines.add(currentLine)
                currentLine = word
            } else {
                currentLine = testLine
            }
        }

        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }

        return lines.ifEmpty { listOf(text) }
    }
}