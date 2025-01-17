package com.example.timetable.utils

import com.example.timetable.model.ApiResponse
import com.example.timetable.model.User
import com.example.timetable.network.ApiClient
import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.TimeoutException
import com.example.timetable.BuildConfig


suspend fun <T> fetchData(
    apiCall: suspend () -> Response<ApiResponse<T>>,
    onSuccess: (T) -> Unit,
    onError: (String) -> Unit,
    onUnauthorized: () -> Unit,
    debug: Boolean = BuildConfig.DEBUG
) {
    try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let { apiResponse ->
                if (!apiResponse.error && apiResponse.data != null) {
                    onSuccess(apiResponse.data)
                } else {
                    onError(apiResponse.message ?: "Unknown error from server")
                }
            } ?: run {
                onError("Empty response from server")
            }
        } else {
            when (response.code()) {
                401 -> onUnauthorized()
                else -> {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (debug) {
                        "HTTP ${response.code()}: ${errorBody ?: response.message()}"
                    } else {
                        "An error occurred. Please try again."
                    }
                    onError(errorMessage)
                }
            }
        }
    } catch (e: IOException) {
        val errorMessage = if (debug) {
            "Network error: ${e.message}"
        } else {
            "No internet connection"
        }
        onError(errorMessage)
    } catch (e: HttpException) {
        val errorMessage = if (debug) {
            "HTTP exception: ${e.code()} - ${e.message()}"
        } else {
            "An error occurred. Please try again."
        }
        onError(errorMessage)
    } catch (e: Exception) {
        val errorMessage = if (debug) {
            "Unexpected error: ${e.message}"
        } else {
            "An unexpected error occurred."
        }
        onError(errorMessage)
    }
}







suspend fun handleLogin(username: String, password: String): Result<ApiResponse<Any>> {
    return try {
        val user = User(username = username, password = password)
        val response = ApiClient.service.loginUser(user)

        if (response.isSuccessful) {
            response.body()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Brak danych w odpowiedzi"))
        } else {
            val errorResponse = response.errorBody()?.string()
            val apiError = errorResponse?.let {
                try {
                    Gson().fromJson(it, ApiResponse::class.java)
                } catch (e: Exception) {
                    null
                }
            }
            val errorMessage = apiError?.message ?: "Błąd logowania: ${response.code()} ${response.message()}"
            Result.failure(Exception(errorMessage))
        }
    } catch (e: IOException) {
        Result.failure(Exception("Brak połączenia z internetem"))
    } catch (e: Exception) {
        Result.failure(Exception("Wystąpił błąd: ${e.message}"))
    }
}

suspend fun logout(token: String, onLogout: () -> Unit) {
    try {
        // Wywołanie API do wylogowania
        val response = ApiClient.service.logoutUser("Bearer $token")
        if (response.isSuccessful) {
            onLogout()
        } else {
            throw HttpException(response)
        }
    } catch (e: IOException) {
        onLogout()
    } catch (e: HttpException) {
        // Obsługa błędnych odpowiedzi API
    } catch (e: TimeoutException) {
        // Obsługa błędów timeoutu
    } catch (e: Exception) {
        // Obsługa innych błędów
    }
}

