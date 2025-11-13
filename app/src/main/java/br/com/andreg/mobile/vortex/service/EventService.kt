package br.com.andreg.mobile.vortex.service

import android.util.Log
import br.com.andreg.mobile.vortex.auth.ErrorResponse
import br.com.andreg.mobile.vortex.auth.SessionManager
import br.com.andreg.mobile.vortex.model.Event
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.coroutines.awaitObjectResult
import com.github.kittinunf.fuel.gson.gsonDeserializerOf
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader

class EventService {

    private val gson = Gson()
    private val eventsUrl = "https://api.vortex.andreg.com.br/events"

    suspend fun getEvents(): List<Event> {
        val token = SessionManager.authToken ?: throw Exception("Token de autenticação não encontrado.")

        Log.d("EventService", token)

        val result = Fuel.get(eventsUrl)
            .awaitObjectResult(object: ResponseDeserializable<List<Event>> {
                override fun deserialize(reader: Reader): List<Event> {
                    val listType = object : TypeToken<List<Event>>() {}.type
                    return gson.fromJson(reader, listType)
                }
            })

        return when (result) {
            is Result.Success -> {
                Log.d("EventService", "Eventos recebidos: ${result.value.size}")
                Log.d("EventService", result.value.toString())
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
                    "Falha ao buscar eventos: ${error.message}"
                }
                Log.e("EventService", errorMessage, error)
                throw Exception(errorMessage)
            }
        }
    }
}
