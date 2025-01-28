package app.edumate.presentation.createCourse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import app.edumate.R
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.components.ProgressDialog
import app.edumate.presentation.theme.EdumateTheme
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateCourseScreen(
    onNavigateToCourseDetails: (courseId: String) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    courseId: String? = null,
    viewModel: CreateCourseViewModel = koinViewModel(),
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnNavigateToCourseDetails by rememberUpdatedState(onNavigateToCourseDetails)

    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { viewModel.uiState }
            .filter { it.newCourseId != null }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnNavigateToCourseDetails(it.newCourseId!!)
            }
    }

    CreateCourseContent(
        uiState = viewModel.uiState,
        onEvent = viewModel::onEvent,
        onNavigateUp = onNavigateUp,
        modifier = modifier,
        courseId = courseId,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateCourseContent(
    uiState: CreateCourseUiState,
    onEvent: (CreateCourseUiEvent) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    courseId: String? = null,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val maxWidthModifier = Modifier.fillMaxWidth()
    val context = LocalContext.current

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(CreateCourseUiEvent.UserMessageShown)
        }
    }

    Scaffold(
        modifier =
            modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    val titleId =
                        if (courseId == null) {
                            R.string.title_create_class_screen
                        } else {
                            R.string.title_edit_class_screen
                        }
                    Text(text = stringResource(id = titleId))
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        if (uiState.isLoading) {
            LoadingScreen()
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                TextField(
                    state = uiState.name,
                    modifier = maxWidthModifier.focusRequester(focusRequester),
                    label = {
                        Text(text = stringResource(R.string.class_name))
                    },
                    supportingText =
                        uiState.nameError?.let {
                            { Text(text = it.asString(), modifier = Modifier.clearAndSetSemantics {}) }
                        },
                    isError = uiState.nameError != null,
                    keyboardOptions =
                        KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next,
                        ),
                    onKeyboardAction = {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                    lineLimits = TextFieldLineLimits.SingleLine,
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    state = uiState.section,
                    modifier = maxWidthModifier,
                    label = {
                        Text(text = stringResource(R.string.section))
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next,
                        ),
                    onKeyboardAction = {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                    lineLimits = TextFieldLineLimits.SingleLine,
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    state = uiState.room,
                    modifier = maxWidthModifier,
                    label = {
                        Text(text = stringResource(R.string.room))
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next,
                        ),
                    onKeyboardAction = {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                    lineLimits = TextFieldLineLimits.SingleLine,
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    state = uiState.subject,
                    modifier = maxWidthModifier,
                    label = {
                        Text(text = stringResource(R.string.subject))
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done,
                        ),
                    onKeyboardAction = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    },
                    lineLimits = TextFieldLineLimits.SingleLine,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onEvent(CreateCourseUiEvent.CreateCourse)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text =
                            if (courseId == null) {
                                stringResource(id = R.string.create)
                            } else {
                                stringResource(id = R.string.save)
                            },
                    )
                }
            }

            LaunchedEffect(true) {
                focusRequester.requestFocus()
            }
        }
    }

    ProgressDialog(
        open = uiState.openProgressDialog,
        onDismissRequest = {},
    )
}

@Preview
@Composable
private fun CreateCourseScreenPreview() {
    EdumateTheme {
        CreateCourseContent(
            uiState = CreateCourseUiState(),
            onEvent = {},
            onNavigateUp = {},
        )
    }
}
