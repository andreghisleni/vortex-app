package br.com.andreg.mobile.vortex.auth

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.coroutines.awaitResponseResult
import com.github.kittinunf.fuel.gson.gsonDeserializerOf
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import java.net.URLDecoder

class AuthService {

    private val gson = Gson()
    private val loginUrl = "https://api.vortex.andreg.com.br/auth/api/sign-in/email"

    suspend fun login(email: String, password: String): SignInResponse {
        val requestBody = SignInEmailRequest(
            email = email,
            password = password
        )
        val jsonBody = gson.toJson(requestBody)
        Log.d("AuthService", "JSON enviado: $jsonBody")

        val (request, response, result) = Fuel.post(loginUrl)
            .body(jsonBody)
            .header(Headers.CONTENT_TYPE, "application/json")
            .awaitResponseResult(gsonDeserializerOf<SignInResponse>(SignInResponse::class.java))

        val cookies = response.headers[Headers.SET_COOKIE]
        if (cookies.isNotEmpty()) {
            Log.d("AuthService", "Cookies recebidos: $cookies")
            val tokenCookie = cookies.firstOrNull { it.startsWith("__Secure-better-auth.session_token=") }
            tokenCookie?.let {
                // 1. Extrai o valor do cookie, que está URL-encoded
                val encodedToken = it.substringAfter("__Secure-better-auth.session_token=").substringBefore(";")
                Log.d("AuthService", "Token (URL-encoded) extraído: $encodedToken")

                // 2. Decodifica o token para reverter caracteres como %3D e %2B
                val decodedToken = URLDecoder.decode(encodedToken, "UTF-8")
                Log.d("AuthService", "Token decodificado: $decodedToken")

                // 3. Salva o token DECODIFICADO na sessão
                SessionManager.saveAuthToken(decodedToken)
            }
        }

        return when (result) {
            is Result.Success -> {
                Log.d("AuthService", "Corpo da resposta: ${result.value}")
                result.value
            }
            is Result.Failure -> {
                val error = result.error
                val errorMessage = try {
                    val errorResponse = gson.fromJson(
                        String(error.response.data),
                        ErrorResponse::class.java
                    )
                    errorResponse.message
                } catch (e: Exception) {
                    "Falha no login: ${error.message}"
                }
                Log.e("AuthService", "Erro de login/API: $errorMessage")
                throw Exception(errorMessage)
            }
        }
    }
}
