package br.com.andreg.mobile.vortex.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
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

    // Função para buscar os eventos
    fun fetchEvents() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                Log.d("EventSelectionScreen", "Buscando eventos...")
                events = eventService.getEvents()
            } catch (e: Exception) {
                Log.e("EventSelectionScreen", "Falha ao buscar eventos", e)
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    // Busca inicial na primeira vez que a tela é carregada
    LaunchedEffect(Unit) {
        fetchEvents()
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
                // UI de Erro com botão para tentar novamente
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Erro ao carregar eventos: $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { fetchEvents() }) {
                        Text("Tentar Novamente")
                    }
                }
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
                            scope.launch {
                                val eventId = event.id.toString()
                                Log.d("EventSelectionScreen", "Salvando Evento ID: $eventId")
                                SessionManager.saveEventId(eventId)
                                onEventSelected()
                            }
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
        Text(text = event.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
