package br.com.andreg.mobile.vortex.auth

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.coroutines.awaitObjectResult // 👈 Usar awaitObjectResult
import com.github.kittinunf.fuel.gson.gsonDeserializerOf
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthService {

    private val gson = Gson()
    private val loginUrl = "https://api.vortex.andreg.com.br/auth/api/sign-in/email"

    /**
     * Realiza uma requisição de login usando e-mail e senha.
     * Usa awaitObjectResult para ser totalmente assíncrono e Thread-safe.
     */
    suspend fun login(email: String, password: String): SignInResponse {
        val requestBody = SignInEmailRequest(
            email = email,
            password = password
        )
        val jsonBody = gson.toJson(requestBody)

        Log.d("AuthService", "JSON enviado: $jsonBody")


        // 💡 CORREÇÃO: Troque .response() por .awaitObjectResult()
        // Isso executa a chamada de rede em uma thread de I/O do Fuel/Coroutines,
        // retornando o resultado.
        val result = Fuel.post(loginUrl)
            .body(jsonBody)
            .header(Headers.CONTENT_TYPE, "application/json") // Boa prática
            .awaitObjectResult(gsonDeserializerOf<SignInResponse>(SignInResponse::class.java)) // 👈 Chamada Suspend

        Log.d("AuthService", "Resultado da requisição: $result")

        return when (result) {
            is Result.Success -> {
                // A API retornou um código 2xx e desserialização foi bem-sucedida.
                result.value
            }
            is Result.Failure -> {
                // A API retornou um código não-2xx OU ocorreu um erro de rede/parsing.
                val error = result.error
                val errorMessage = try {
                    // Tenta extrair a mensagem de erro do corpo da resposta (se houver)
                    val errorResponse = gson.fromJson(
                        String(error.response.data),
                        ErrorResponse::class.java
                    )
                    errorResponse.message
                } catch (e: Exception) {
                    // Se falhar, usa a mensagem de erro do Fuel/Kotlin.
                    "Falha no login ou na rede: ${error.message}"
                }
                Log.e("AuthService", "Erro de login/API: $errorMessage")
                throw Exception(errorMessage)
            }
        }
    }
}