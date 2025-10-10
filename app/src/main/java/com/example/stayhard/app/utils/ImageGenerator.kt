package com.example.stayhard.app.utils

import android.content.Context
import android.graphics.*
import com.example.stayhard.app.model.Quote
import com.example.stayhard.app.model.Style
import com.example.stayhard.app.utils.Constants.Image

object ImageGenerator {

    /**
     * Génère une image bitmap à partir d'une citation
     * Applique le style fourni ou utilise les valeurs par défaut
     */
    fun generateQuoteImage(context: Context, quote: Quote): Bitmap {
        val bitmap = Bitmap.createBitmap(Image.WIDTH, Image.HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Utiliser un style par défaut si null
        val style = quote.style ?: createDefaultStyle()

        // Appliquer le fond (uni ou dégradé)
        applyBackground(canvas, style, Image.WIDTH, Image.HEIGHT)

        // Dessiner le texte avec ombre
        drawTextWithShadow(canvas, quote, style)

        return bitmap
    }

    /**
     * Crée un style par défaut
     */
    private fun createDefaultStyle(): Style {
        return Style(
            fontFamily = Constants.DefaultStyle.FONT_FAMILY,
            fontSize = Constants.DefaultStyle.FONT_SIZE,
            textColor = Constants.DefaultStyle.TEXT_COLOR,
            backgroundColor = Constants.DefaultStyle.BACKGROUND_COLOR,
            shape = Constants.DefaultStyle.SHAPE,
            position = Constants.DefaultStyle.POSITION
        )
    }

    /**
     * Applique le fond d'écran (dégradé ou couleur unie)
     */
    private fun applyBackground(canvas: Canvas, style: Style, width: Int, height: Int) {
        val gradient = style.backgroundGradient

        if (gradient != null) {
            // ✅ Dégradé
            val paint = Paint().apply {
                isAntiAlias = true
                val startColor = parseColorSafe(gradient.startColor)
                val endColor = parseColorSafe(gradient.endColor)

                shader = when (gradient.direction) {
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
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        } else {
            // ✅ Couleur unie
            val bgColor = parseColorSafe(style.backgroundColor ?: Constants.DefaultStyle.BACKGROUND_COLOR)
            canvas.drawColor(bgColor)
        }
    }

    /**
     * Dessine le texte avec ombre portée
     */
    private fun drawTextWithShadow(canvas: Canvas, quote: Quote, style: Style) {
        val textPaint = createTextPaint(style, false)
        val shadowPaint = createTextPaint(style, true)

        val centerX = canvas.width / 2f
        val centerY = canvas.height / 2f

        // Diviser le texte en lignes
        val maxWidth = canvas.width - Image.MARGIN
        val lines = wrapText(quote.texte, textPaint, maxWidth)

        val lineHeight = textPaint.descent() - textPaint.ascent()
        val totalTextHeight = lines.size * lineHeight
        val startY = centerY - (totalTextHeight / 2) + lineHeight

        // Dessiner chaque ligne avec ombre
        lines.forEachIndexed { index, line ->
            val y = startY + (index * lineHeight)
            // Ombre
            canvas.drawText(line, centerX + Image.SHADOW_OFFSET, y + Image.SHADOW_OFFSET, shadowPaint)
            // Texte principal
            canvas.drawText(line, centerX, y, textPaint)
        }

        // Dessiner l'auteur
        drawAuthor(canvas, quote.auteur, style, startY + (lines.size * lineHeight), centerX)
    }

    /**
     * Dessine le nom de l'auteur
     */
    private fun drawAuthor(canvas: Canvas, author: String, style: Style, startY: Float, centerX: Float) {
        val authorPaint = Paint().apply {
            color = parseColorSafe(style.textColor ?: Constants.DefaultStyle.TEXT_COLOR)
            textSize = (style.fontSize ?: Constants.DefaultStyle.FONT_SIZE).toFloat() *
                    Image.TEXT_SIZE_MULTIPLIER * Image.AUTHOR_SIZE_RATIO
            typeface = Typeface.create(style.fontFamily ?: Constants.DefaultStyle.FONT_FAMILY, Typeface.ITALIC)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val shadowPaint = Paint(authorPaint).apply {
            color = Color.argb(Image.SHADOW_ALPHA, 0, 0, 0)
        }

        val authorY = startY + Image.AUTHOR_SPACING
        val authorText = "— $author"

        // Ombre
        canvas.drawText(authorText, centerX + 2f, authorY + 2f, shadowPaint)
        // Texte
        canvas.drawText(authorText, centerX, authorY, authorPaint)
    }

    /**
     * Crée un Paint pour le texte
     */
    private fun createTextPaint(style: Style, isShadow: Boolean): Paint {
        return Paint().apply {
            color = if (isShadow) {
                Color.argb(Image.SHADOW_ALPHA, 0, 0, 0)
            } else {
                parseColorSafe(style.textColor ?: Constants.DefaultStyle.TEXT_COLOR)
            }
            textSize = (style.fontSize ?: Constants.DefaultStyle.FONT_SIZE).toFloat() * Image.TEXT_SIZE_MULTIPLIER
            typeface = Typeface.create(style.fontFamily ?: Constants.DefaultStyle.FONT_FAMILY, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
    }

    /**
     * Divise le texte en lignes pour qu'il tienne dans la largeur max
     */
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

    /**
     * Parse une couleur de manière sécurisée avec fallback
     */
    private fun parseColorSafe(colorString: String): Int {
        return try {
            Color.parseColor(colorString)
        } catch (e: IllegalArgumentException) {
            // Fallback sur blanc si parsing échoue
            Color.parseColor(Constants.DefaultStyle.TEXT_COLOR)
        }
    }
}