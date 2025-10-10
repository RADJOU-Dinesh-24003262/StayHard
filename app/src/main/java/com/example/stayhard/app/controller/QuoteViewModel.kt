package com.example.stayhard.app.controller

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.stayhard.app.model.Quote
import com.example.stayhard.app.network.ApiServiceFactory
import kotlinx.coroutines.launch

class QuoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuoteRepository(ApiServiceFactory.create())
    private val _quote = MutableLiveData<Quote>()
    val quote: LiveData<Quote> get() = _quote

    fun fetchQuote(onResult: (Quote?) -> Unit) {
        viewModelScope.launch {
            try {
                val quote = repository.getRandomQuote()
                _quote.value = quote
                onResult(quote)
            } catch (e: Exception) {
                Log.e("QuoteViewModel", "Erreur lors du chargement: ${e.message}", e)
                onResult(null)
            }
        }
    }
}