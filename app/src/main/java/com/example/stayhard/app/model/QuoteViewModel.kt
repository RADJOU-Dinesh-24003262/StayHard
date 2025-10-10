package com.example.stayhard.app.model

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
    
    // ✅ Utiliser Result au lieu de Quote nullable
    private val _quoteResult = MutableLiveData<Result<Quote>>()
    val quoteResult: LiveData<Result<Quote>> get() = _quoteResult
    
    /**
     * Récupère une citation aléatoire
     * Gestion d'erreur robuste avec Result
     */
    fun fetchQuote() {
        viewModelScope.launch {
            // ✅ Émettre Loading
            _quoteResult.value = Result.Loading
            
            try {
                val quote = repository.getRandomQuote()
                // ✅ Émettre Success
                _quoteResult.value = Result.Success(quote)
                
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
     * Version avec callback pour compatibilité
     * @deprecated Utiliser quoteResult LiveData à la place
     */
    @Deprecated("Utiliser quoteResult LiveData")
    fun fetchQuote(onResult: (Quote?) -> Unit) {
        viewModelScope.launch {
            try {
                val quote = repository.getRandomQuote()
                onResult(quote)
            } catch (e: Exception) {
                Log.e(Constants.LogTags.QUOTE_VIEWMODEL, "Erreur: ${e.message}", e)
                onResult(null)
            }
        }
    }
}