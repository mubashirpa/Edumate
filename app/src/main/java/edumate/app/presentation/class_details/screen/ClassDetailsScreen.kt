package edumate.app.presentation.class_details.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import edumate.app.core.DataState
import edumate.app.navigation.ClassDetailsNavHost
import edumate.app.presentation.class_details.ClassDetailsUiEvent
import edumate.app.presentation.class_details.ClassDetailsUiState
import edumate.app.presentation.class_details.screen.components.BottomNavigationBar
import edumate.app.presentation.components.EdumateSnackbarHost
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClassDetailsScreen(
    // Here we are using another NavHost so we need a separate NavHostController
    classDetailsNavController: NavHostController = rememberNavController(),
    uiState: ClassDetailsUiState,
    onEvent: (ClassDetailsUiEvent) -> Unit,
    onLeaveClass: () -> Unit,
    onBackPressed: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    when (val dataState = uiState.dataState) {
        is DataState.EMPTY -> {
            ErrorScreen(
                modifier = Modifier.fillMaxSize(),
                errorMessage = dataState.message.asString()
            )
        }

        is DataState.ERROR -> {
            ErrorScreen(
                modifier = Modifier.fillMaxSize(),
                errorMessage = dataState.message.asString(),
                onRetry = {
                    onEvent(ClassDetailsUiEvent.OnRetry)
                }
            )
        }

        DataState.LOADING -> {
            LoadingIndicator(modifier = Modifier.fillMaxSize())
        }

        DataState.SUCCESS -> {
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(navController = classDetailsNavController)
                },
                snackbarHost = {
                    EdumateSnackbarHost(snackbarHostState)
                },
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
                        )
                ) {
                    ClassDetailsNavHost(
                        navController = classDetailsNavController,
                        modifier = Modifier.fillMaxSize(),
                        snackbarHostState = snackbarHostState,
                        uiState = uiState,
                        onEvent = onEvent,
                        onLeaveClass = onLeaveClass,
                        onBackPressed = onBackPressed
                    )
                }
            }
        }

        DataState.UNKNOWN -> {}
    }
}