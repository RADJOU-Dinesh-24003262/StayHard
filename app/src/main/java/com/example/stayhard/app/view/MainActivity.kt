package com.example.stayhard.app.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.stayhard.app.R
import com.example.stayhard.app.controller.QuoteViewModel
import com.example.stayhard.app.controller.WallpaperUpdateReceiver
import com.example.stayhard.app.utils.ImageGenerator
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var fabRefresh: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var viewModel: QuoteViewModel

    private var currentToast: Toast? = null
    private var isLoading = false
    private var currentWallpaperBitmap: Bitmap? = null // Stocker l'image actuelle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialiser les vues
        imageView = findViewById(R.id.imageView)
        fabRefresh = findViewById(R.id.fabRefresh)
        progressBar = findViewById(R.id.progressBar)

        viewModel = ViewModelProvider(this)[QuoteViewModel::class.java]

        setupWallpaperChange()
        setupRefreshButton()
        updateQuoteDisplay()
    }

    override fun onPause() {
        super.onPause()
        // L'app passe en arrière-plan → définir le fond d'écran
        setLockScreenWallpaper()
    }

    override fun onBackPressed() {
        // L'utilisateur appuie sur retour → définir le fond d'écran puis fermer
        setLockScreenWallpaper()
        super.onBackPressed()
    }

    private fun setLockScreenWallpaper() {
        currentWallpaperBitmap?.let { bitmap ->
            try {
                val wallpaperManager = WallpaperManager.getInstance(this)

                // Définir uniquement pour l'écran de verrouillage
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                } else {
                    // Pour les versions plus anciennes, définir comme fond d'écran principal
                    wallpaperManager.setBitmap(bitmap)
                }

                // Notification discrète
                showToast("Fond d'écran mis à jour")

            } catch (e: Exception) {
                // En cas d'erreur silencieuse (pas de perturbation pour l'utilisateur)
                showToast("Erreur lors de la mise à jour du fond d'écran")
            }
        }
    }

    private fun setupRefreshButton() {
        fabRefresh.setOnClickListener {
            if (!isLoading) { // Empêcher le spam
                refreshQuote()
            }
        }
    }

    private fun refreshQuote() {
        isLoading = true
        showLoading(true)
        updateQuoteDisplay()
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            progressBar.visibility = View.VISIBLE
            fabRefresh.hide()
        } else {
            progressBar.visibility = View.GONE
            fabRefresh.show()
            isLoading = false
        }
    }

    private fun showToast(message: String) {
        // Annuler le toast précédent s'il existe
        currentToast?.cancel()
        // Créer et afficher le nouveau toast
        currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }

    private fun setupWallpaperChange() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, WallpaperUpdateReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR,
            AlarmManager.INTERVAL_HOUR,
            pendingIntent
        )
    }

    private fun updateQuoteDisplay() {
        viewModel.fetchQuote { quote ->
            runOnUiThread {
                showLoading(false)
                if (quote != null) {
                    // Générer l'image avec le dégradé
                    val bitmap = ImageGenerator.generateQuoteImage(this@MainActivity, quote)
                    // Stocker l'image pour le fond d'écran
                    currentWallpaperBitmap = bitmap
                    // Afficher l'image générée
                    imageView.setImageBitmap(bitmap)

                    // Toast de confirmation (sans spam)
                    showToast("Citation mise à jour !")
                } else {
                    // En cas d'erreur
                    showToast("Erreur lors du chargement")
                }
            }
        }
    }
}