package edumate.app.presentation.create_class.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.presentation.components.EdumateSnackbarHost
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.create_class.CreateClassUiEvent
import edumate.app.presentation.create_class.CreateClassUiState
import kotlinx.coroutines.flow.Flow

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun CreateClassScreen(
    uiState: CreateClassUiState,
    onEvent: (CreateClassUiEvent) -> Unit,
    createClassResults: Flow<String>,
    courseId: String? = null,
    navigateToClassDetails: (courseId: String) -> Unit,
    onBackPressed: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {
        }
    }

    LaunchedEffect(context) {
        createClassResults.collect { courseId ->
            navigateToClassDetails(courseId)
        }
    }

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the
            onEvent(CreateClassUiEvent.UserMessageShown)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    val titleId = if (courseId == null) {
                        Strings.title_create_class_screen
                    } else {
                        Strings.title_edit_class_screen
                    }
                    Text(text = stringResource(id = titleId))
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = Strings.navigate_up)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { EdumateSnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (uiState.loading) {
                LoadingIndicator(modifier = Modifier.fillMaxSize())
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                ) {
                    @Suppress("SENSELESS_COMPARISON")
                    TextField(
                        value = uiState.name,
                        onValueChange = {
                            onEvent(CreateClassUiEvent.NameChanged(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        label = {
                            Text(text = stringResource(id = Strings.class_name))
                        },
                        supportingText = if (uiState.nameError != null) {
                            { Text(text = uiState.nameError.asString()) }
                        } else {
                            null
                        },
                        isError = uiState.nameError != null,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = uiState.section,
                        onValueChange = {
                            onEvent(CreateClassUiEvent.SectionChanged(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = Strings.section))
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = uiState.room,
                        onValueChange = {
                            onEvent(CreateClassUiEvent.RoomChanged(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = Strings.room))
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = uiState.subject,
                        onValueChange = {
                            onEvent(CreateClassUiEvent.SubjectChanged(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = Strings.subject))
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    val buttonText = if (courseId == null) {
                        Strings.create
                    } else {
                        Strings.save
                    }
                    Button(
                        onClick = {
                            onEvent(CreateClassUiEvent.OnCreateClick)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = buttonText))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    ProgressDialog(openDialog = uiState.openProgressDialog)
}