package edumate.app.presentation.joinClass

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.components.EdumateSnackbarHost
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.components.UserAvatar
import kotlinx.coroutines.flow.Flow
import edumate.app.R.string as Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinClassScreen(
    uiState: JoinClassUiState,
    onEvent: (JoinClassUiEvent) -> Unit,
    joinClassResults: Flow<String>,
    navigateToClassDetails: (courseId: String) -> Unit,
    navigateToProfile: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isStudent = uiState.userType == UserType.STUDENT
    val bottomSheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )
    val isClassCodeError = uiState.classCodeError != null

    LaunchedEffect(context) {
        focusRequester.requestFocus()
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
                            id = uiState.currentUser?.uid.orEmpty(),
                            fullName =
                                uiState.currentUser?.displayName
                                    ?: uiState.currentUser?.email.orEmpty(),
                            photoUri = uiState.currentUser?.photoUrl,
                            modifier = Modifier.clickable(onClick = navigateToProfile),
                            size = 30.dp,
                            textStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { EdumateSnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
        ) {
            AssistChip(
                onClick = { onEvent(JoinClassUiEvent.OnOpenUserTypeBottomSheetChange(true)) },
                label = {
                    Text(
                        text =
                            if (isStudent) {
                                stringResource(id = Strings.student)
                            } else {
                                stringResource(id = Strings.teacher)
                            },
                    )
                },
                trailingIcon = {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        Modifier.size(AssistChipDefaults.IconSize),
                    )
                },
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text =
                    if (isStudent) {
                        stringResource(id = Strings.enter_the_code_shared_by_your_teacher)
                    } else {
                        stringResource(id = Strings.enter_the_code_shared_by_the_class_owner)
                    },
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = uiState.classCode,
                onValueChange = {
                    onEvent(JoinClassUiEvent.OnClassCodeChange(it))
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
                            Text(
                                text =
                                    if (isStudent) {
                                        stringResource(id = Strings.ask_your_teacher_for_the_class_code)
                                    } else {
                                        stringResource(id = Strings.ask_the_class_owner_for_the_class_code)
                                    },
                            )
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
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (uiState.openUserTypeBottomSheet) {
        val bottomMargin =
            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 10.dp

        ModalBottomSheet(
            onDismissRequest = { onEvent(JoinClassUiEvent.OnOpenUserTypeBottomSheetChange(false)) },
            sheetState = bottomSheetState,
            windowInsets = WindowInsets(0),
        ) {
            Column(modifier = Modifier.selectableGroup()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = isStudent,
                            onClick = {
                                onEvent(JoinClassUiEvent.OnUserTypeChange(UserType.STUDENT))
                                onEvent(JoinClassUiEvent.OnOpenUserTypeBottomSheetChange(false))
                            },
                            role = Role.RadioButton,
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = isStudent,
                        onClick = null,
                    )
                    Text(
                        text = stringResource(id = Strings.student),
                        modifier = Modifier.padding(start = 16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = !isStudent,
                            onClick = {
                                onEvent(JoinClassUiEvent.OnUserTypeChange(UserType.TEACHER))
                                onEvent(JoinClassUiEvent.OnOpenUserTypeBottomSheetChange(false))
                            },
                            role = Role.RadioButton,
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = !isStudent,
                        onClick = null,
                    )
                    Text(
                        text = stringResource(id = Strings.teacher),
                        modifier = Modifier.padding(start = 16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Spacer(modifier = Modifier.height(bottomMargin))
            }
        }
    }

    ProgressDialog(openDialog = uiState.openProgressDialog)
}
