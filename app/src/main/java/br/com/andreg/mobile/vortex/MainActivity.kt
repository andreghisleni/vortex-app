package br.com.andreg.mobile.vortex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import br.com.andreg.mobile.vortex.ui.screens.AuthScreen
import br.com.andreg.mobile.vortex.ui.screens.EventSelectionScreen
import br.com.andreg.mobile.vortex.ui.screens.FavoritesScreen
import br.com.andreg.mobile.vortex.ui.screens.HomeScreen
import br.com.andreg.mobile.vortex.ui.screens.ProfileScreen
import br.com.andreg.mobile.vortex.ui.theme.VortexTheme

import java.net.CookieManager
import java.net.CookiePolicy
import java.net.CookieHandler

fun setupCookieManagement() {
    // 1. Cria uma instância do CookieManager.
    // Opcionalmente, você pode fornecer um CookieStore para persistência em disco.
    val cookieManager = CookieManager()

    // 2. Define a política de cookies para aceitar todos (ou apenas os de origem, dependendo do caso).
    cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

    // 3. Define este CookieManager como o padrão para todas as conexões HTTP/HTTPS.
    // O Fuel (que usa HttpURLConnection) irá respeitar esta configuração.
    CookieHandler.setDefault(cookieManager)

    // Agora, após o login, o cookie do servidor será armazenado,
    // e em requisições futuras, ele será enviado automaticamente.
}

enum class AppState {
    AUTHENTICATING,
    EVENT_SELECTION,
    MAIN_APP
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupCookieManagement()
        setContent {
            VortexTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    var appState by rememberSaveable { mutableStateOf(AppState.AUTHENTICATING) }

    when (appState) {
        AppState.AUTHENTICATING -> {
            AuthScreen(
                modifier = Modifier.fillMaxSize(),
                onAuthComplete = { appState = AppState.EVENT_SELECTION }
            )
        }
        AppState.EVENT_SELECTION -> {
            EventSelectionScreen(
                modifier = Modifier.fillMaxSize(),
                onEventSelected = { appState = AppState.MAIN_APP }
            )
        }
        AppState.MAIN_APP -> {
            VortexApp()
        }
    }
}

@PreviewScreenSizes
@Composable
fun VortexApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold {
            innerPadding ->
            when (currentDestination) {
                AppDestinations.HOME -> HomeScreen(modifier = Modifier.padding(innerPadding))
                AppDestinations.FAVORITES -> FavoritesScreen(modifier = Modifier.padding(innerPadding))
                AppDestinations.PROFILE -> ProfileScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}
