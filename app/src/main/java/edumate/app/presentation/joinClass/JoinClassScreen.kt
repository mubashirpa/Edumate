package edumate.app.presentation.joinClass

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import edumate.app.presentation.components.EdumateSnackbarHost
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.components.UserAvatar
import edumate.app.presentation.ui.theme.EdumateTheme
import kotlinx.coroutines.flow.filter
import edumate.app.R.string as Strings

@Composable
fun JoinClassScreen(
    viewModel: JoinClassViewModel = hiltViewModel(),
    navigateToClassDetails: (courseId: String) -> Unit,
    navigateToProfile: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentNavigateToClassDetails by rememberUpdatedState(navigateToClassDetails)
    val context = LocalContext.current
    val snackbarHostState =
        remember {
            SnackbarHostState()
        }

    LaunchedEffect(viewModel, lifecycle) {
        // Whenever the uiState changes, check if the user is joined class and call the
        // `navigateToClassDetails` event when `lifecycle` is at least STARTED
        snapshotFlow { viewModel.uiState }
            .filter { it.joinClassId != null }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentNavigateToClassDetails(it.joinClassId!!)
            }
    }

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(JoinClassUiEvent.UserMessageShown)
        }
    }

    JoinClassScreenContent(
        uiState = viewModel.uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        navigateToProfile = navigateToProfile,
        onBackPressed = onBackPressed,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JoinClassScreenContent(
    uiState: JoinClassUiState,
    onEvent: (JoinClassUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    navigateToProfile: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isClassCodeError = uiState.classCodeError != null
    val focusRequester =
        remember {
            FocusRequester()
        }

    Scaffold(
        modifier =
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = Strings.title_join_class_screen))
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = Strings.navigate_up),
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        UserAvatar(
                            id = uiState.currentUser?.id.orEmpty(),
                            fullName =
                                uiState.currentUser?.name?.fullName
                                    ?: uiState.currentUser?.emailAddress.orEmpty(),
                            photoUrl = uiState.currentUser?.photoUrl,
                            modifier = Modifier.clickable(onClick = navigateToProfile),
                            size = 30.dp,
                            textStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = {
            EdumateSnackbarHost(snackbarHostState)
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Text(
                text = stringResource(id = Strings.ask_your_teacher_for_the_class_code_then_enter_it_here),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = uiState.classCode,
                onValueChange = {
                    onEvent(JoinClassUiEvent.OnClassCodeValueChange(it))
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                label = {
                    Text(text = stringResource(id = Strings.class_code))
                },
                supportingText =
                    if (isClassCodeError) {
                        {
                            Text(text = stringResource(id = Strings.ask_your_teacher_for_the_class_code))
                        }
                    } else {
                        null
                    },
                isError = isClassCodeError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions =
                    KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        },
                    ),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    onEvent(JoinClassUiEvent.JoinClass)
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(id = Strings.join))
            }
        }
    }

    ProgressDialog(openDialog = uiState.openProgressDialog)

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
private fun JoinClassScreenPreview() {
    EdumateTheme {
        JoinClassScreenContent(
            uiState = JoinClassUiState(),
            onEvent = {},
            snackbarHostState = SnackbarHostState(),
            navigateToProfile = {},
            onBackPressed = {},
        )
    }
}
