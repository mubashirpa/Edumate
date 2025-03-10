package app.edumate.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import app.edumate.navigation.Graph
import app.edumate.navigation.Screen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.main.components.EdumateApp
import app.edumate.presentation.theme.EdumateTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { viewModel.uiState.isLoading }

        enableEdgeToEdge()
        setupContent()
    }

    private fun setupContent() {
        setContent {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }
            val startDestination = determineStartDestination()
            val uiState = viewModel.uiState

            EdumateTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                ) { innerPadding ->
                    if (uiState.isLoading) {
                        LoadingScreen(modifier = Modifier.padding(innerPadding))
                    } else {
                        EdumateApp(
                            uiState = uiState,
                            onEvent = viewModel::onEvent,
                            navController = navController,
                            startDestination = startDestination,
                            snackbarHostState = snackbarHostState,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }

    private fun determineStartDestination(): Any =
        if (viewModel.uiState.isUserLoggedIn) {
            Screen.Home(null)
        } else {
            Graph.Authentication
        }
}
