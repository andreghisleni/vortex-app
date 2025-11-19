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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import br.com.andreg.mobile.vortex.auth.SessionManager
import br.com.andreg.mobile.vortex.data.preferences.UserPreferencesRepository
import br.com.andreg.mobile.vortex.ui.screens.AuthScreen
import br.com.andreg.mobile.vortex.ui.screens.EventSelectionScreen
import br.com.andreg.mobile.vortex.ui.screens.FavoritesScreen
import br.com.andreg.mobile.vortex.ui.screens.HomeScreen
import br.com.andreg.mobile.vortex.ui.screens.ProfileScreen
import br.com.andreg.mobile.vortex.ui.screens.SplashScreen
import br.com.andreg.mobile.vortex.ui.theme.VortexTheme

enum class AppState {
    LOADING,
    AUTHENTICATING,
    EVENT_SELECTION,
    MAIN_APP
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VortexTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    var appState by remember { mutableStateOf(AppState.LOADING) }

    // Este LaunchedEffect só executa uma vez, na inicialização.
    LaunchedEffect(Unit) {
        // Inicializa o SessionManager com a persistência.
        SessionManager.initialize(UserPreferencesRepository(context))

        // Carrega os dados e decide a rota inicial.
        val hasFullSession = SessionManager.loadSessionData()
        if (hasFullSession) {
            appState = AppState.MAIN_APP
        } else if (SessionManager.authToken != null) {
            appState = AppState.EVENT_SELECTION
        } else {
            appState = AppState.AUTHENTICATING
        }
    }

    when (appState) {
        AppState.LOADING -> SplashScreen()
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
    var currentDestination by remember { mutableStateOf(AppDestinations.HOME) }

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
