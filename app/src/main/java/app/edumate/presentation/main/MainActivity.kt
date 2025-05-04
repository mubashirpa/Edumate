package app.edumate.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import app.edumate.R
import app.edumate.navigation.Graph
import app.edumate.navigation.Screen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.main.components.EdumateApp
import app.edumate.presentation.theme.EdumateTheme
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()
    private val updateManager: AppUpdateManager by inject()

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
            val coroutineScope = rememberCoroutineScope()
            val startDestination = determineStartDestination()
            val uiState = viewModel.uiState
            val updateLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                    if (result.resultCode != RESULT_OK) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(getString(R.string.update_failed))
                        }
                    }
                }

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

            LaunchedEffect(uiState.updateAvailable) {
                if (uiState.updateAvailable && uiState.updateInfo != null) {
                    updateManager.startUpdateFlowForResult(
                        uiState.updateInfo,
                        updateLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                    )
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
