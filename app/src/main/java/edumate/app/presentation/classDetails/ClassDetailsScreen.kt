package edumate.app.presentation.classDetails

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import edumate.app.core.Result
import edumate.app.navigation.ClassDetailsNavHost
import edumate.app.presentation.classDetails.components.ClassDetailsNavigationBar
import edumate.app.presentation.components.EdumateSnackbarHost
import edumate.app.presentation.components.ErrorScreen
import edumate.app.R.string as Strings

@Composable
fun ClassDetailsScreen(
    uiState: ClassDetailsUiState,
    onEvent: (ClassDetailsUiEvent) -> Unit,
    onLeaveClass: () -> Unit,
    onBackPressed: () -> Unit,
) {
    when (val courseResult = uiState.courseResult) {
        is Result.Empty -> {
            // Nothing is shown
        }

        is Result.Error -> {
            ErrorScreen(
                onRetryClick = {
                    onEvent(ClassDetailsUiEvent.Retry)
                },
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                errorMessage = courseResult.message!!.asString(),
            )
        }

        is Result.Loading -> {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
            ) {
                CircularProgressIndicator()
            }
        }

        is Result.Success -> {
            val course = courseResult.data
            if (course != null) {
                ClassDetailsScreenContent(
                    uiState = uiState,
                    onEvent = onEvent,
                    onLeaveClass = onLeaveClass,
                    onBackPressed = onBackPressed,
                )
            } else {
                ErrorScreen(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    errorMessage = stringResource(Strings.class_not_found),
                )
            }
        }
    }
}

@Composable
private fun ClassDetailsScreenContent(
    // Separate NavHostController for nested navigation
    classDetailsNavController: NavHostController = rememberNavController(),
    uiState: ClassDetailsUiState,
    onEvent: (ClassDetailsUiEvent) -> Unit,
    onLeaveClass: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val snackbarHostState =
        remember {
            SnackbarHostState()
        }

    Scaffold(
        bottomBar = {
            ClassDetailsNavigationBar(navController = classDetailsNavController)
        },
        snackbarHost = {
            EdumateSnackbarHost(hostState = snackbarHostState)
        },
        contentWindowInsets = WindowInsets(0),
    ) { innerPadding ->
        ClassDetailsNavHost(
            navController = classDetailsNavController,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            snackbarHostState = snackbarHostState,
            uiState = uiState,
            onEvent = onEvent,
            onLeaveClass = onLeaveClass,
            onBackPressed = onBackPressed,
        )
    }
}
