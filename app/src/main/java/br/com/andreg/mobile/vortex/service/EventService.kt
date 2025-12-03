package br.com.andreg.mobile.vortex.service

import android.util.Log
import br.com.andreg.mobile.vortex.BuildConfig
import br.com.andreg.mobile.vortex.auth.ErrorResponse
import br.com.andreg.mobile.vortex.auth.SessionManager
import br.com.andreg.mobile.vortex.model.Event
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResult
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader

class EventService {

    private val gson = Gson()
    private val eventsUrl = BuildConfig.BASE_URL + "/events"

    suspend fun getEvents(): List<Event> {
        val token = SessionManager.authToken ?: throw Exception("Token de autenticação não encontrado.")

        Log.d("EventService", "Enviando token como cookie: $token")

        val result = Fuel.get(eventsUrl)
            // CORREÇÃO: Enviando o token como um Cookie em vez de um Header de Autorização.
            .header(Headers.COOKIE, "__Secure-better-auth.session_token=$token")
            .awaitObjectResult(object : ResponseDeserializable<List<Event>> {
                override fun deserialize(reader: Reader): List<Event> {
                    val listType = object : TypeToken<List<Event>>() {}.type
                    return gson.fromJson(reader, listType)
                }
            })

        return when (result) {
            is Result.Success -> {
                Log.d("EventService", "Eventos recebidos: ${result.value.size}")
                val events = result.value.map { event ->
                    event.copy(description = event.description ?: " ")
                }
                Log.d("EventService", events.toString())
                events
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
                    "Falha ao buscar eventos: ${error.message}"
                }
                Log.e("EventService", errorMessage, error)
                throw Exception(errorMessage)
            }
        }
    }
}
