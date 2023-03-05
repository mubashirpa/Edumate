package edumate.app.presentation.create_class.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
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
import androidx.hilt.navigation.compose.hiltViewModel
import edumate.app.R.string as Strings
import edumate.app.presentation.components.EdumateSnackbarHost
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.create_class.CreateClassUiEvent
import edumate.app.presentation.create_class.CreateClassViewModel

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun CreateClassScreen(
    viewModel: CreateClassViewModel = hiltViewModel(),
    navigateToRoom: (roomId: String) -> Unit,
    onBackPressed: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(context) {
        viewModel.createRoomResults.collect { roomId ->
            navigateToRoom(roomId)
        }
    }

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(CreateClassUiEvent.UserMessageShown)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = Strings.title_create_class_screen))
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
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                val nameError = viewModel.uiState.nameError
                @Suppress("SENSELESS_COMPARISON")
                TextField(
                    value = viewModel.uiState.name,
                    onValueChange = {
                        viewModel.onEvent(CreateClassUiEvent.NameChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    label = {
                        Text(text = stringResource(id = Strings.class_name))
                    },
                    supportingText = if (nameError != null) {
                        {
                            Text(text = nameError.asString())
                        }
                    } else {
                        null
                    },
                    isError = nameError != null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = viewModel.uiState.section,
                    onValueChange = {
                        viewModel.onEvent(CreateClassUiEvent.SectionChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
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
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = viewModel.uiState.room,
                    onValueChange = {
                        viewModel.onEvent(CreateClassUiEvent.RoomChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
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
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = viewModel.uiState.subject,
                    onValueChange = {
                        viewModel.onEvent(CreateClassUiEvent.SubjectChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
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
                Spacer(modifier = Modifier.height(20.dp))
            }

            FAB(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .imePadding()
                    .padding(16.dp),
                expanded = viewModel.uiState.isFabExpanded
            ) {
                viewModel.onEvent(CreateClassUiEvent.OnCreateClick)
            }
        }
    }

    ProgressDialog(
        text = stringResource(id = Strings.creating_room),
        openDialog = viewModel.uiState.openProgressDialog
    )
}

@Composable
private fun FAB(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        expanded = expanded,
        icon = {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = stringResource(id = Strings.create)
            )
        },
        text = { Text(text = stringResource(id = Strings.create)) }
    )
}