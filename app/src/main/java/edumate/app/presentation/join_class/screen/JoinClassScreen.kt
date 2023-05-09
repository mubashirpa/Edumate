package edumate.app.presentation.join_class.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edumate.app.R.string as Strings
import edumate.app.presentation.components.EdumateSnackbarHost
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.components.UserAvatar
import edumate.app.presentation.join_class.JoinClassUiEvent
import edumate.app.presentation.join_class.JoinClassUiState
import kotlinx.coroutines.flow.Flow

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun JoinClassScreen(
    uiState: JoinClassUiState,
    onEvent: (JoinClassUiEvent) -> Unit,
    joinClassResults: Flow<String>,
    navigateToClassDetails: (courseId: String) -> Unit,
    navigateToProfile: () -> Unit,
    onBackPressed: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(context) {
        joinClassResults.collect { courseId ->
            navigateToClassDetails(courseId)
        }
    }

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(JoinClassUiEvent.UserMessageShown)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = Strings.title_join_class_screen))
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = Strings.navigate_up)
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        UserAvatar(
                            id = uiState.currentUser?.uid.orEmpty(),
                            fullName = uiState.currentUser?.displayName
                                ?: uiState.currentUser?.email.orEmpty(),
                            photoUri = uiState.currentUser?.photoUrl,
                            modifier = Modifier.clickable(onClick = navigateToProfile),
                            size = 30.dp,
                            textStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp)
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
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = uiState.classCode,
                    onValueChange = {
                        onEvent(JoinClassUiEvent.ClassCodeChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    label = {
                        Text(text = stringResource(id = Strings.class_code))
                    },
                    supportingText = if (uiState.classCodeError != null) {
                        {
                            Text(
                                text = stringResource(
                                    id = Strings.ask_your_teacher_for_the_class_code
                                )
                            )
                        }
                    } else {
                        {
                            Text(
                                text = stringResource(
                                    id = Strings.class_code_shared_by_your_teacher
                                )
                            )
                        }
                    },
                    isError = uiState.classCodeError != null,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    onClick = {
                        onEvent(JoinClassUiEvent.OnJoinClick)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = Strings.join))
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    ProgressDialog(openDialog = uiState.openProgressDialog)
}