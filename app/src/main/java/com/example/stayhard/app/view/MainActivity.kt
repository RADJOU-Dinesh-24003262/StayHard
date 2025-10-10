package com.example.stayhard.app.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.stayhard.app.R
import com.example.stayhard.app.controller.QuoteViewModel
import com.example.stayhard.app.utils.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    // Views
    private lateinit var imageView: ImageView
    private lateinit var fabRefresh: FloatingActionButton
    private lateinit var progressBar: ProgressBar

    // ViewModel
    private lateinit var viewModel: QuoteViewModel

    // ✅ Gestionnaire de Bitmap pour éviter les leaks
    private val bitmapManager = BitmapManager()

    // État
    private var currentToast: Toast? = null
    private var isLoading = false

    // ✅ Flag pour éviter les opérations après destruction
    private var isDestroyed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isDestroyed = false

        initViews()
        initViewModel()
        setupAlarms()
        setupRefreshButton()
        loadInitialQuote()

        Log.d("MainActivity", "onCreate completed")
    }

    /**
     * Initialise les vues
     */
    private fun initViews() {
        imageView = findViewById(R.id.imageView)
        fabRefresh = findViewById(R.id.fabRefresh)
        progressBar = findViewById(R.id.progressBar)
    }

    /**
     * Initialise le ViewModel avec LiveData observer
     */
    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[QuoteViewModel::class.java]

        // ✅ Observer le LiveData (meilleure pratique)
        viewModel.quote.observe(this) { quote ->
            if (!isDestroyed && quote != null) {
                handleQuoteSuccess(quote)
            }
        }
    }

    /**
     * Configure les alarmes pour le changement automatique
     */
    private fun setupAlarms() {
        try {
            AlarmManagerHelper.setupWallpaperAlarms(this)
            Log.d("MainActivity", "Alarmes configurées")
        } catch (e: Exception) {
            Log.e("MainActivity", "Erreur configuration alarmes: ${e.message}", e)
        }
    }

    /**
     * Configure le bouton refresh
     */
    private fun setupRefreshButton() {
        fabRefresh.setOnClickListener {
            if (!isLoading && !isDestroyed) {
                refreshQuote()
            }
        }
    }

    /**
     * Rafraîchit la citation
     */
    private fun refreshQuote() {
        if (isDestroyed) return

        isLoading = true
        showLoading(true)

        // ✅ Utiliser le callback mais avec protection
        viewModel.fetchQuote { quote ->
            if (!isDestroyed && !isFinishing) {
                runOnUiThread {
                    showLoading(false)
                    if (quote != null) {
                        handleQuoteSuccess(quote)
                    } else {
                        handleQuoteError(Constants.Messages.QUOTE_ERROR)
                    }
                }
            }
        }
    }

    /**
     * Charge la première citation
     */
    private fun loadInitialQuote() {
        if (isDestroyed) return

        viewModel.fetchQuote { quote ->
            if (!isDestroyed && !isFinishing) {
                runOnUiThread {
                    showLoading(false)
                    if (quote != null) {
                        handleQuoteSuccess(quote)
                    } else {
                        handleQuoteError(Constants.Messages.QUOTE_ERROR)
                    }
                }
            }
        }
    }

    /**
     * Gère le succès du chargement d'une citation
     */
    private fun handleQuoteSuccess(quote: com.example.stayhard.app.model.Quote) {
        if (isDestroyed) return

        try {
            // Générer l'image
            val bitmap = ImageGenerator.generateQuoteImage(this, quote)

            // ✅ Mettre à jour via BitmapManager (recycle l'ancien)
            bitmapManager.updateBitmap(bitmap)

            // Afficher dans l'ImageView
            imageView.setImageBitmap(bitmap)

            // Toast de confirmation
            showToast(Constants.Messages.QUOTE_UPDATED)

            Log.d("MainActivity", "Citation chargée avec succès")

        } catch (e: Exception) {
            Log.e("MainActivity", "Erreur lors du traitement de la citation: ${e.message}", e)
            handleQuoteError("Erreur lors de la génération de l'image")
        }
    }

    /**
     * Gère l'erreur de chargement
     */
    private fun handleQuoteError(message: String) {
        if (isDestroyed) return
        showToast(message)
        Log.e("MainActivity", "Erreur: $message")
    }

    /**
     * Affiche/masque l'indicateur de chargement
     */
    private fun showLoading(show: Boolean) {
        if (isDestroyed) return

        try {
            if (show) {
                progressBar.visibility = View.VISIBLE
                fabRefresh.hide()
            } else {
                progressBar.visibility = View.GONE
                fabRefresh.show()
                isLoading = false
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Erreur showLoading: ${e.message}", e)
        }
    }

    /**
     * Affiche un toast (annule le précédent)
     */
    private fun showToast(message: String) {
        if (isDestroyed || isFinishing) return

        try {
            currentToast?.cancel()
            currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT).apply { show() }
        } catch (e: Exception) {
            Log.e("MainActivity", "Erreur showToast: ${e.message}", e)
        }
    }

    // ═════════════════════════════════════════════════════════════
    // LIFECYCLE - Gestion du fond d'écran
    // ═════════════════════════════════════════════════════════════

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause()")

        // ✅ Définir le fond d'écran quand l'app passe en arrière-plan
        // Mais seulement si pas en cours de destruction
        if (!isFinishing && !isDestroyed) {
            setWallpaperIfAvailable()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop()")
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        Log.d("MainActivity", "onBackPressed()")

        // ✅ Définir le fond d'écran avant de quitter
        setWallpaperIfAvailable()

        super.onBackPressed()
    }

    override fun onDestroy() {
        Log.d("MainActivity", "onDestroy() - début")

        // ✅ Marquer comme détruit AVANT tout nettoyage
        isDestroyed = true

        // ✅ Nettoyer les ressources
        try {
            bitmapManager.clear()
            currentToast?.cancel()
            currentToast = null
        } catch (e: Exception) {
            Log.e("MainActivity", "Erreur dans onDestroy: ${e.message}", e)
        }

        super.onDestroy()

        Log.d("MainActivity", "onDestroy() - fin")
    }

    /**
     * Définit le fond d'écran si un bitmap est disponible
     * @param showNotification Afficher ou non un toast (false en arrière-plan)
     */
    private fun setWallpaperIfAvailable(showNotification: Boolean = true) {
        if (isDestroyed) {
            Log.d("MainActivity", "setWallpaperIfAvailable - Activity détruite, skip")
            return
        }

        if (!bitmapManager.hasBitmap()) {
            Log.d("MainActivity", "setWallpaperIfAvailable - Pas de bitmap disponible")
            return
        }

        try {
            bitmapManager.getCurrentBitmap()?.let { bitmap ->
                Log.d("MainActivity", "setWallpaperIfAvailable - Tentative de définir le wallpaper")

                val success = WallpaperManagerHelper.setLockScreenWallpaper(this, bitmap)

                if (success) {
                    Log.d("MainActivity", "Wallpaper défini avec succès")
                    // ✅ Afficher toast seulement si demandé ET si l'app est visible
                    if (showNotification && !isFinishing) {
                        showToast(Constants.Messages.WALLPAPER_UPDATED)
                    }
                } else {
                    Log.e("MainActivity", "Échec de la définition du wallpaper")
                    if (showNotification && !isFinishing) {
                        showToast(Constants.Messages.WALLPAPER_ERROR)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Exception lors de setWallpaperIfAvailable: ${e.message}", e)
        }
    }
}