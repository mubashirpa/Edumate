package app.edumate.presentation.main

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import app.edumate.BuildConfig
import app.edumate.navigation.Graph
import app.edumate.navigation.Screen
import app.edumate.presentation.main.components.EdumateApp
import app.edumate.presentation.main.components.RequestNotificationPermissionDialog
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

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        InitializeNotification(
                            uiState = viewModel.uiState,
                            onEvent = viewModel::onEvent,
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

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun InitializeNotification(
    uiState: MainUiState,
    onEvent: (MainUiEvent) -> Unit,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current as ComponentActivity
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                openNotificationSettings(context)
            }
        }

    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED -> {
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                POST_NOTIFICATIONS,
            ) -> {
                onEvent(MainUiEvent.OnOpenRequestNotificationPermissionDialogChange(true))
            }

            else -> {
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }

    RequestNotificationPermissionDialog(
        open = uiState.openRequestNotificationPermissionDialog,
        onDismissRequest = {
            onEvent(MainUiEvent.OnOpenRequestNotificationPermissionDialogChange(false))
        },
        onConfirmation = {
            requestPermissionLauncher.launch(POST_NOTIFICATIONS)
        },
    )
}

private fun openNotificationSettings(context: Context) {
    val intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
        }
    context.startActivity(intent)
}
