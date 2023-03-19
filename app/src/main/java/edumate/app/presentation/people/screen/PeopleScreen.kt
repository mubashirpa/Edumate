package edumate.app.presentation.people.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import edumate.app.R.string as Strings
import edumate.app.domain.model.User
import edumate.app.domain.model.courses.Course
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.people.DataState
import edumate.app.presentation.people.PeopleFilter
import edumate.app.presentation.people.PeopleUiEvent
import edumate.app.presentation.people.PeopleViewModel
import edumate.app.presentation.people.screen.components.PeopleListItem
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun PeopleScreen(
    viewModel: PeopleViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    course: Course,
    onLeaveClass: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnLeaveClass by rememberUpdatedState(onLeaveClass)
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.uiState.refreshing,
        onRefresh = {
            viewModel.onEvent(PeopleUiEvent.OnRefresh)
        }
    )
    val currentUserType =
        if (course.teachers?.contains(viewModel.uiState.currentUser?.uid) == true) {
            UserType.TEACHER
        } else {
            UserType.STUDENT
        }

    LaunchedEffect(viewModel, lifecycle) {
        // Whenever the uiState changes, check if the user is leave class and
        // call the `onLeaveClass` event when `lifecycle` is at least STARTED
        snapshotFlow { viewModel.uiState }.filter { it.isUserLeaveClass }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnLeaveClass()
            }
    }

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(PeopleUiEvent.UserMessageShown)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        when (val dataState = viewModel.uiState.dataState) {
            is DataState.UNKNOWN -> {
                // Nothing happened
            }
            is DataState.LOADING -> {
                LoadingIndicator()
            }
            is DataState.ERROR -> {
                ErrorScreen(
                    errorMessage = dataState.message.asString(),
                    onRetry = {
                        viewModel.onEvent(PeopleUiEvent.OnRetry)
                    }
                )
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.padding(vertical = 10.dp)) {
                        Spacer(modifier = Modifier.width(16.dp))
                        FilterChip(
                            selected = viewModel.uiState.filter == PeopleFilter.ALL,
                            onClick = {
                                viewModel.onEvent(
                                    PeopleUiEvent.OnFilterChange(PeopleFilter.ALL)
                                )
                            },
                            label = { Text(stringResource(id = Strings.all)) },
                            leadingIcon = if (viewModel.uiState.filter == PeopleFilter.ALL) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = null,
                                        modifier = Modifier.size(
                                            FilterChipDefaults.IconSize
                                        )
                                    )
                                }
                            } else {
                                null
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = viewModel.uiState.filter == PeopleFilter.TEACHERS,
                            onClick = {
                                viewModel.onEvent(
                                    PeopleUiEvent.OnFilterChange(PeopleFilter.TEACHERS)
                                )
                            },
                            label = { Text(stringResource(id = Strings.teachers)) },
                            leadingIcon = if (viewModel.uiState.filter == PeopleFilter.TEACHERS) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = null,
                                        modifier = Modifier.size(
                                            FilterChipDefaults.IconSize
                                        )
                                    )
                                }
                            } else {
                                null
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = viewModel.uiState.filter == PeopleFilter.STUDENTS,
                            onClick = {
                                viewModel.onEvent(
                                    PeopleUiEvent.OnFilterChange(PeopleFilter.STUDENTS)
                                )
                            },
                            label = { Text(stringResource(id = Strings.students)) },
                            leadingIcon = if (viewModel.uiState.filter == PeopleFilter.STUDENTS) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = null,
                                        modifier = Modifier.size(
                                            FilterChipDefaults.IconSize
                                        )
                                    )
                                }
                            } else {
                                null
                            }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    if (dataState is DataState.EMPTY) {
                        ErrorScreen(errorMessage = dataState.message.asString())
                    } else {
                        Box(modifier = Modifier.pullRefresh(refreshState)) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                content = {
                                    items(
                                        items = viewModel.uiState.peoples,
                                        key = { it.id.orEmpty() }
                                    ) { user: User ->
                                        val userType =
                                            if (course.teachers?.contains(user.id) == true) {
                                                UserType.TEACHER
                                            } else {
                                                UserType.STUDENT
                                            }

                                        PeopleListItem(
                                            user = user,
                                            modifier = Modifier.animateItemPlacement(),
                                            currentUserType = currentUserType,
                                            currentUserId = "${viewModel.uiState.currentUser?.uid}",
                                            courseOwnerId = course.ownerId.orEmpty(),
                                            onLeaveClass = {
                                                viewModel.onEvent(
                                                    PeopleUiEvent.OnOpenLeaveClassDialogChange(
                                                        true
                                                    )
                                                )
                                            },
                                            onEmail = {
                                                composeEmail(
                                                    context,
                                                    arrayOf(user.emailAddress.orEmpty())
                                                )
                                            },
                                            onRemove = {
                                                // TODO("Add confirmation dialog")
                                                viewModel.onEvent(
                                                    PeopleUiEvent.OnDeletePeople(
                                                        userType,
                                                        user.id.orEmpty()
                                                    )
                                                )
                                            },
                                            onClick = {
                                                // TODO("Not yet implemented")
                                            }
                                        )
                                    }
                                }
                            )

                            PullRefreshIndicator(
                                viewModel.uiState.refreshing,
                                refreshState,
                                Modifier.align(Alignment.TopCenter)
                            )
                        }
                    }
                }
            }
        }

        if (currentUserType == UserType.TEACHER) {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.onEvent(PeopleUiEvent.OnOpenFabMenuChange(true))
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .imePadding()
                    .padding(16.dp),
                expanded = viewModel.uiState.isFabExpanded,
                icon = {
                    Icon(
                        imageVector = Icons.Default.PersonAddAlt,
                        contentDescription = stringResource(id = Strings.invite)
                    )
                },
                text = { Text(text = stringResource(id = Strings.invite)) }
            )
        }
    }

    if (viewModel.uiState.openFabMenu) {
        // TODO("Fix alignment")
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.onEvent(PeopleUiEvent.OnOpenFabMenuChange(false))
            }
        ) {
            val courseLink = course.alternateLink.orEmpty()
            ListItem(
                headlineContent = { Text(text = stringResource(id = Strings.share)) },
                modifier = Modifier.clickable {
                    viewModel.onEvent(PeopleUiEvent.OnOpenFabMenuChange(false))
                    share(context, courseLink)
                }
            )
            ListItem(
                headlineContent = { Text(text = stringResource(id = Strings.copy_link)) },
                modifier = Modifier.clickable {
                    viewModel.onEvent(PeopleUiEvent.OnOpenFabMenuChange(false))
                    copy(context, courseLink) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                context.getString(Strings.copied_invitation_link)
                            )
                        }
                    }
                }
            )
            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }
    }

    ProgressDialog(
        text = viewModel.uiState.progressDialogText.asString(),
        openDialog = viewModel.uiState.openProgressDialog
    )

    LeaveClassDialog(
        openDialog = viewModel.uiState.openLeaveClassDialog,
        onDismissRequest = {
            viewModel.onEvent(PeopleUiEvent.OnOpenLeaveClassDialogChange(false))
        },
        onConfirmClick = {
            viewModel.uiState.currentUser?.uid?.let {
                viewModel.onEvent(PeopleUiEvent.OnLeaveClass(it))
            }
        }
    )
}

@Composable
private fun LeaveClassDialog(
    openDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = Strings.leave_class))
            },
            text = {
                Text(text = stringResource(id = Strings.leave_class_warning_message))
            },
            confirmButton = {
                TextButton(onClick = onConfirmClick) {
                    Text(stringResource(id = Strings.leave_class))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = Strings.cancel))
                }
            }
        )
    }
}

private fun share(context: Context, text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

private fun copy(context: Context, textCopied: String, onSuccess: () -> Unit) {
    val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.setPrimaryClip(ClipData.newPlainText(textCopied, textCopied))
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        onSuccess()
    }
}

private fun composeEmail(context: Context, addresses: Array<String>) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // only email apps should handle this
        putExtra(Intent.EXTRA_EMAIL, addresses)
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}