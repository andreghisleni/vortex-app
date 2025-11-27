package br.com.andreg.mobile.vortex.auth

import android.util.Log
import br.com.andreg.mobile.vortex.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.first

/**
 * Objeto singleton para gerenciar o estado e a persistência da sessão do usuário.
 */
object SessionManager {

    var authToken: String? = null
    var eventId: String? = null

    private lateinit var prefsRepository: UserPreferencesRepository

    fun initialize(repository: UserPreferencesRepository) {
        this.prefsRepository = repository
    }

    suspend fun loadSessionData(): Boolean {
        authToken = prefsRepository.authToken.first()
        eventId = prefsRepository.eventId.first()
        return authToken != null && eventId != null
    }

    suspend fun saveAuthToken(token: String) {
        authToken = token
        if (::prefsRepository.isInitialized) {
            prefsRepository.saveAuthToken(token)
        }
    }

    suspend fun saveEventId(id: String) {
        eventId = id
        if (::prefsRepository.isInitialized) {
            prefsRepository.saveEventId(id)
        }
    }

    /**
     * Limpa a sessão atual e os dados persistidos (logout).
     */
    suspend fun logout() {
        Log.d("SessionManager", "Limpando sessão e dados persistidos.")
        authToken = null
        eventId = null
        if (::prefsRepository.isInitialized) {
            prefsRepository.clear()
        }
    }
}
