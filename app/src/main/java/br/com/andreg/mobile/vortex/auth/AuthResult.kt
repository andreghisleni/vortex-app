package br.com.andreg.mobile.vortex.auth

/**
 * Data class para o corpo da requisição de login.
 */
data class SignInEmailRequest(
    val email: String,
    val password: String,
)

/**
 * Data class para a resposta de sucesso do login.
 */
data class SignInResponse(
    val token: String,
    val user: User
)

/**
 * Data class para o objeto de usuário na resposta de login.
 */
data class User(
    val id: String,
    val email: String,
    val name: String?,
    val image: String?,
    val emailVerified: Boolean,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Data class para a resposta de erro da API.
 */
data class ErrorResponse(
    val message: String
)
