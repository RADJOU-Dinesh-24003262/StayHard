package com.example.stayhard.app.utils

/**
 * Sealed class pour gérer les résultats d'opérations asynchrones
 * Alternative type-safe aux callbacks avec null
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

/**
 * Extensions pour faciliter l'usage
 */
fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

fun <T> Result<T>.onError(action: (Exception, String?) -> Unit): Result<T> {
    if (this is Result.Error) {
        action(exception, message)
    }
    return this
}

fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) {
        action()
    }
    return this
}

/**
 * Types d'erreurs spécifiques
 */
sealed class AppException : Exception() {
    data class NetworkException(override val message: String) : AppException()
    data class ApiException(val code: Int, override val message: String) : AppException()
    data class ParseException(override val message: String) : AppException()
    data class WallpaperException(override val message: String) : AppException()
    object NoInternetException : AppException() {
        override val message: String = "Pas de connexion internet"
    }
}