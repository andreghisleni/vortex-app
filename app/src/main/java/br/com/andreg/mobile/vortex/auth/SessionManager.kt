package br.com.andreg.mobile.vortex.auth

import br.com.andreg.mobile.vortex.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.first

/**
 * Objeto singleton para gerenciar o estado e a persistência da sessão do usuário.
 */
object SessionManager {

    var authToken: String? = null
    var eventId: String? = null

    // Referência privada para o nosso repositório de persistência.
    private lateinit var prefsRepository: UserPreferencesRepository

    /**
     * Inicializa o SessionManager com o repositório de preferências.
     * DEVE ser chamado uma única vez na inicialização do app.
     */
    fun initialize(repository: UserPreferencesRepository) {
        this.prefsRepository = repository
    }

    /**
     * Carrega os dados persistidos (token e eventId) para a sessão atual.
     * Retorna `true` se o usuário já tem uma sessão completa (token + evento).
     */
    suspend fun loadSessionData(): Boolean {
        authToken = prefsRepository.authToken.first()
        eventId = prefsRepository.eventId.first()
        return authToken != null && eventId != null
    }

    /**
     * Salva o token de autenticação na sessão e o persiste no DataStore.
     */
    suspend fun saveAuthToken(token: String) {
        authToken = token
        prefsRepository.saveAuthToken(token)
    }

    /**
     * Salva o ID do evento na sessão e o persiste no DataStore.
     */
    suspend fun saveEventId(id: String) {
        eventId = id
        prefsRepository.saveEventId(id)
    }
}
