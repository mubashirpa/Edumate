package edumate.app.presentation.createClass

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import edumate.app.presentation.components.EdumateSnackbarHost
import edumate.app.presentation.components.ProgressDialog
import kotlinx.coroutines.flow.Flow
import edumate.app.R.string as Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateClassScreen(
    uiState: CreateClassUiState,
    onEvent: (CreateClassUiEvent) -> Unit,
    createClassResults: Flow<String>,
    courseId: String? = null,
    navigateToClassDetails: (courseId: String) -> Unit,
    onBackPressed: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isNameError = uiState.nameError != null
    val buttonText =
        if (courseId == null) {
            Strings.create
        } else {
            Strings.save
        }

    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {
        }
    }

    LaunchedEffect(context) {
        createClassResults.collect {
            navigateToClassDetails(it)
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
        modifier =
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    val titleId =
                        if (courseId == null) {
                            Strings.title_create_class_screen
                        } else {
                            Strings.title_edit_class_screen
                        }
                    Text(text = stringResource(id = titleId))
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = Strings.navigate_up),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { EdumateSnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        if (uiState.loading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .imePadding()
                        .verticalScroll(rememberScrollState()),
            ) {
                TextField(
                    value = uiState.name,
                    onValueChange = {
                        onEvent(CreateClassUiEvent.OnNameValueChange(it))
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                    label = {
                        Text(text = stringResource(id = Strings.class_name))
                    },
                    supportingText =
                        if (isNameError) {
                            { Text(text = uiState.nameError!!.asString()) }
                        } else {
                            null
                        },
                    isError = isNameError,
                    keyboardOptions =
                        KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next,
                        ),
                    keyboardActions =
                        KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = uiState.section,
                    onValueChange = {
                        onEvent(CreateClassUiEvent.OnSectionValueChange(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(id = Strings.section))
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next,
                        ),
                    keyboardActions =
                        KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = uiState.room,
                    onValueChange = {
                        onEvent(CreateClassUiEvent.OnRoomValueChange(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(id = Strings.room))
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next,
                        ),
                    keyboardActions =
                        KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = uiState.subject,
                    onValueChange = {
                        onEvent(CreateClassUiEvent.OnSubjectValueChange(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(id = Strings.subject))
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done,
                        ),
                    keyboardActions =
                        KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        onEvent(CreateClassUiEvent.OnCreateClick)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(id = buttonText))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    ProgressDialog(openDialog = uiState.openProgressDialog)
}
