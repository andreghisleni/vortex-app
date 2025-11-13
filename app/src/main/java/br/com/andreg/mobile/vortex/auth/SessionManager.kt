package br.com.andreg.mobile.vortex.auth

/**
 * Objeto singleton para armazenar informações da sessão atual do usuário,
 * como o token de autenticação e o ID do evento selecionado.
 */
object SessionManager {
    var authToken: String? = null
    var eventId: String? = null
}
