package br.com.andreg.mobile.vortex.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import br.com.andreg.mobile.vortex.R

/**
 * Uma tela em Compose que hospeda um layout XML tradicional.
 */
@Composable
fun CreateEventScreen(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            // Infla o layout XML
            android.view.View.inflate(context, R.layout.fragment_create_event, null)
        },
        update = {
            // Você pode adicionar lógica aqui para interagir com as Views do layout XML
            // por exemplo: view.findViewById<Button>(R.id.my_button).setOnClickListener { ... }
        }
    )
}
