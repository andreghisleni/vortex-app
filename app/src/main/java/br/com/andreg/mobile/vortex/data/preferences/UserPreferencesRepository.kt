package br.com.andreg.mobile.vortex.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property para criar uma instância do DataStore a nível de aplicação
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {

    // Define as chaves para os nossos dados
    private object Keys {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val EVENT_ID = stringPreferencesKey("event_id")
    }

    // Expõe um Flow para ler o token de autenticação
    val authToken: Flow<String?> = context.dataStore.data.map {
        preferences -> preferences[Keys.AUTH_TOKEN]
    }

    // Expõe um Flow para ler o ID do evento
    val eventId: Flow<String?> = context.dataStore.data.map {
        preferences -> preferences[Keys.EVENT_ID]
    }

    /**
     * Salva o token de autenticação no DataStore.
     */
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit {
            preferences -> preferences[Keys.AUTH_TOKEN] = token
        }
    }

    /**
     * Salva o ID do evento selecionado no DataStore.
     */
    suspend fun saveEventId(id: String) {
        context.dataStore.edit {
            preferences -> preferences[Keys.EVENT_ID] = id
        }
    }

    /**
     * Limpa todos os dados de preferência do usuário (usado para logout).
     */
    suspend fun clear() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
