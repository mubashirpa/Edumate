package app.edumate.presentation.main.components

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import app.edumate.BuildConfig
import app.edumate.navigation.EdumateNavHost
import app.edumate.presentation.main.MainUiEvent
import app.edumate.presentation.main.MainUiState
import org.koin.compose.KoinContext

@Composable
fun EdumateApp(
    uiState: MainUiState,
    onEvent: (MainUiEvent) -> Unit,
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

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        InitializeNotification(
            uiState = uiState,
            onEvent = onEvent,
        )
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

    LaunchedEffect(uiState.notificationPermissionRequested) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED ||
                uiState.notificationPermissionRequested -> Unit

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                POST_NOTIFICATIONS,
            ) -> {
                onEvent(MainUiEvent.OnOpenRequestNotificationPermissionDialogChange(true))
                onEvent(MainUiEvent.OnNotificationPermissionRequestedChange(true))
            }

            else -> {
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
                onEvent(MainUiEvent.OnNotificationPermissionRequestedChange(true))
            }
        }
    }

    RequestNotificationPermissionDialog(
        open = uiState.openRequestNotificationPermissionDialog,
        onDismissRequest = {
            onEvent(MainUiEvent.OnOpenRequestNotificationPermissionDialogChange(false))
        },
        onConfirmation = {
            onEvent(MainUiEvent.OnOpenRequestNotificationPermissionDialogChange(false))
            requestPermissionLauncher.launch(POST_NOTIFICATIONS)
        },
    )
}

private fun openNotificationSettings(context: Context) {
    val intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:${BuildConfig.APPLICATION_ID}".toUri()
        }
    context.startActivity(intent)
}
