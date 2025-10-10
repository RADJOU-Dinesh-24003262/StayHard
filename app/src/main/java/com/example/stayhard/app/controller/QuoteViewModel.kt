package com.example.stayhard.app.controller

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.stayhard.app.di.Dependencies
import com.example.stayhard.app.model.Quote
import com.example.stayhard.app.utils.AppException
import com.example.stayhard.app.utils.Constants
import com.example.stayhard.app.utils.Result
import kotlinx.coroutines.launch
import java.io.IOException

class QuoteViewModel(application: Application) : AndroidViewModel(application) {

    // ✅ Injection via Dependencies
    private val repository = Dependencies.provideQuoteRepository()

    // ✅ LiveData avec Result pour gestion d'état robuste
    private val _quoteResult = MutableLiveData<Result<Quote>>()
    val quoteResult: LiveData<Result<Quote>> get() = _quoteResult

    // ✅ LiveData legacy (pour compatibilité)
    private val _quote = MutableLiveData<Quote>()
    val quote: LiveData<Quote> get() = _quote

    /**
     * Récupère une citation aléatoire (nouvelle version avec Result)
     * Gestion d'erreur robuste avec LiveData
     */
    fun fetchQuote() {
        viewModelScope.launch {
            // ✅ Émettre Loading
            _quoteResult.value = Result.Loading

            try {
                val quote = repository.getRandomQuote()

                // ✅ Émettre Success
                _quoteResult.value = Result.Success(quote)
                _quote.value = quote

            } catch (e: IOException) {
                // ✅ Erreur réseau
                Log.e(Constants.LogTags.QUOTE_VIEWMODEL, "Erreur réseau: ${e.message}", e)
                _quoteResult.value = Result.Error(
                    AppException.NetworkException("Impossible de contacter le serveur"),
                    "Vérifiez votre connexion internet"
                )

            } catch (e: Exception) {
                // ✅ Autre erreur
                Log.e(Constants.LogTags.QUOTE_VIEWMODEL, "Erreur inattendue: ${e.message}", e)
                _quoteResult.value = Result.Error(
                    AppException.ParseException(e.message ?: "Erreur inconnue"),
                    "Impossible de charger la citation"
                )
            }
        }
    }

    /**
     * Version avec callback pour compatibilité avec l'ancien code
     * Utilise la nouvelle architecture en interne
     */
    fun fetchQuote(onResult: (Quote?) -> Unit) {
        viewModelScope.launch {
            _quoteResult.value = Result.Loading

            try {
                val quote = repository.getRandomQuote()
                _quoteResult.value = Result.Success(quote)
                _quote.value = quote
                onResult(quote)

            } catch (e: Exception) {
                Log.e(Constants.LogTags.QUOTE_VIEWMODEL, "Erreur: ${e.message}", e)
                _quoteResult.value = Result.Error(
                    AppException.ParseException(e.message ?: "Erreur"),
                    Constants.Messages.QUOTE_ERROR
                )
                onResult(null)
            }
        }
    }
}