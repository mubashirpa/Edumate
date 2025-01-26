package app.edumate.presentation.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import app.edumate.navigation.EdumateNavHost
import app.edumate.navigation.Graph
import app.edumate.navigation.Screen
import app.edumate.presentation.theme.EdumateTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.KoinContext

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { viewModel.uiState.isLoading }

        enableEdgeToEdge()
        setupContent()
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    private fun setupContent() {
        setContent {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }
            val startDestination = determineStartDestination()

            EdumateTheme {
                Scaffold(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .imePadding(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                ) {
                    if (!viewModel.uiState.isLoading) {
                        EdumateApp(
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

@Composable
private fun EdumateApp(
    navController: NavHostController,
    startDestination: Any,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    KoinContext {
        EdumateNavHost(
            navController = navController,
            snackbarHostState = snackbarHostState,
            modifier = modifier,
            startDestination = startDestination,
        )
    }
}
