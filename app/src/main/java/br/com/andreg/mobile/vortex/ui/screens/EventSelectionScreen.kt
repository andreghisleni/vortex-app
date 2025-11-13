package br.com.andreg.mobile.vortex.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.andreg.mobile.vortex.auth.SessionManager
import br.com.andreg.mobile.vortex.model.Event
import br.com.andreg.mobile.vortex.service.EventService
import kotlinx.coroutines.launch

@Composable
fun EventSelectionScreen(
    modifier: Modifier = Modifier,
    onEventSelected: () -> Unit
) {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val eventService = remember { EventService() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            Log.d("EventSelectionScreen", "Buscando eventos...")
            scope.launch {
                try {
                    events = eventService.getEvents()

                    Log.d("EventSelectionScreen", "Eventos encontrados: ${events.size}")
                } catch (e: Exception) {
                    Log.e("AuthScreen", "Falha no login: ${e.message}")
                    errorMessage = e.message ?: "Ocorreu um erro desconhecido."
                } finally {
                    isLoading = false
                }
            }
        } catch (e: Exception) {
            Log.e("EventSelectionScreen", "Falha ao buscar eventos", e)
            errorMessage = e.message
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            errorMessage != null -> {
                Text(
                    text = "Erro: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Text(
                            text = "Selecione um Evento",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    items(events) { event ->
                        EventItem(event = event) {
                            Log.d("EventSelectionScreen", "Evento selecionado: ${event.name}")
                            SessionManager.eventId = event.id.toString()
                            onEventSelected()
                        }
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun EventItem(event: Event, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp)
    ) {
        Text(text = event.name, style = MaterialTheme.typography.titleMedium)
        if(event.description!=null){
            Text(text = event.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
