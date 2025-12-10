package br.com.andreg.mobile.vortex

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.fragment.app.FragmentActivity
import br.com.andreg.mobile.vortex.auth.SessionManager
import br.com.andreg.mobile.vortex.data.preferences.UserPreferencesRepository
import br.com.andreg.mobile.vortex.ui.member.GetMembersScreen
import br.com.andreg.mobile.vortex.ui.member.MemberFormScreen
import br.com.andreg.mobile.vortex.ui.screens.AuthScreen
import br.com.andreg.mobile.vortex.ui.screens.EventSelectionScreen
import br.com.andreg.mobile.vortex.ui.screens.FavoritesScreen
import br.com.andreg.mobile.vortex.ui.screens.HomeScreen
import br.com.andreg.mobile.vortex.ui.screens.ProfileScreen
import br.com.andreg.mobile.vortex.ui.screens.SplashScreen
import br.com.andreg.mobile.vortex.ui.theme.VortexTheme
import kotlinx.coroutines.launch

enum class AppState {
    LOADING,
    AUTHENTICATING,
    EVENT_SELECTION,
    MAIN_APP
}

class MainActivity : FragmentActivity() {
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
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        SessionManager.initialize(UserPreferencesRepository(context))
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
            VortexApp(
                onSwitchEvent = { appState = AppState.EVENT_SELECTION },
                onLogout = {
                    scope.launch {
                        SessionManager.logout()
                        appState = AppState.AUTHENTICATING
                    }
                }
            )
        }
    }
}

@PreviewScreenSizes
@Composable
fun VortexApp(
    onSwitchEvent: () -> Unit,
    onLogout: () -> Unit
) {
    var currentDestination by remember { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.filter { !it.isJavaDestination }.forEach {
                item(
                    icon = {
                        Icon(it.icon, contentDescription = it.label)
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold { innerPadding ->
            when (currentDestination) {
                AppDestinations.HOME -> HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateToJava = {
                        currentDestination = AppDestinations.MEMBER_FORM
                    }
                )
                AppDestinations.FAVORITES -> FavoritesScreen(modifier = Modifier.padding(innerPadding))
                AppDestinations.PROFILE -> ProfileScreen(
                    modifier = Modifier.padding(innerPadding),
                    onSwitchEvent = onSwitchEvent,
                    onLogout = onLogout
                )
                AppDestinations.MEMBER_FORM -> {
                    MemberFormScreen(
                        eventId = SessionManager.eventId!!, // eventId is guaranteed to be non-null here
                        memberJson = null,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppDestinations.GET_MEMBERS -> {
                    GetMembersScreen(
                        eventId = SessionManager.eventId!!, // eventId is guaranteed to be non-null here
                        modifier = Modifier.padding(innerPadding),
                        onNavigateToMemberForm = {
                            currentDestination = AppDestinations.MEMBER_FORM
                        }
                    )
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val isJavaDestination: Boolean = false
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
    MEMBER_FORM("", Icons.Default.Add, isJavaDestination = true),
    GET_MEMBERS("Members", Icons.Default.List)
}
