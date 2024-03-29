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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import edumate.app.R.string as Strings
import edumate.app.core.Constants
import edumate.app.core.DataState
import edumate.app.domain.model.courses.Course
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.components.AnimatedErrorScreen
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.people.PeopleFilterType
import edumate.app.presentation.people.PeopleUiEvent
import edumate.app.presentation.people.PeopleViewModel
import edumate.app.presentation.people.screen.components.LeaveClassDialog
import edumate.app.presentation.people.screen.components.PeopleListItem
import edumate.app.presentation.people.screen.components.RemoveUserDialog
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
    onLeaveClass: () -> Unit,
    onBackPressed: () -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topBarState)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnLeaveClass by rememberUpdatedState(onLeaveClass)
    val scrollState = rememberLazyListState()
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.uiState.refreshing,
        onRefresh = {
            viewModel.onEvent(PeopleUiEvent.OnRefresh)
        }
    )
    val isTeacher = course.teacherGroupId.contains(viewModel.uiState.currentUser?.uid)
    val expandedFab by remember { derivedStateOf { scrollState.firstVisibleItemIndex == 0 } }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

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

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = course.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
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
                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(
                                PeopleUiEvent.OnAppBarMenuExpandedChange(true)
                            )
                        }
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                    DropdownMenu(
                        expanded = viewModel.uiState.appBarMenuExpanded,
                        onDismissRequest = {
                            viewModel.onEvent(PeopleUiEvent.OnAppBarMenuExpandedChange(false))
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = Strings.refresh)) },
                            onClick = {
                                viewModel.onEvent(PeopleUiEvent.OnAppBarMenuExpandedChange(false))
                                viewModel.onEvent(PeopleUiEvent.OnRefresh)
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.surface
            ),
            scrollBehavior = scrollBehavior
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(refreshState)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            when (val dataState = viewModel.uiState.dataState) {
                is DataState.ERROR -> {
                    ErrorScreen(
                        onRetryClick = { viewModel.onEvent(PeopleUiEvent.OnRetry) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        errorMessage = dataState.message.asString()
                    )
                }

                DataState.LOADING -> {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }

                DataState.UNKNOWN -> {}

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Spacer(modifier = Modifier.width(16.dp))
                            FilterChip(
                                selected = viewModel.uiState.filter == PeopleFilterType.ALL,
                                onClick = {
                                    viewModel.onEvent(
                                        PeopleUiEvent.OnFilterChange(
                                            PeopleFilterType.ALL
                                        )
                                    )
                                },
                                label = { Text(stringResource(id = Strings.all)) },
                                leadingIcon = if (viewModel.uiState.filter == PeopleFilterType.ALL) {
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
                            FilterChip(
                                selected = viewModel.uiState.filter == PeopleFilterType.TEACHERS,
                                onClick = {
                                    viewModel.onEvent(
                                        PeopleUiEvent.OnFilterChange(
                                            PeopleFilterType.TEACHERS
                                        )
                                    )
                                },
                                label = {
                                    Text(
                                        stringResource(id = Strings.teachers)
                                    )
                                },
                                leadingIcon = if (viewModel.uiState.filter == PeopleFilterType.TEACHERS) {
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
                            FilterChip(
                                selected = viewModel.uiState.filter == PeopleFilterType.STUDENTS,
                                onClick = {
                                    viewModel.onEvent(
                                        PeopleUiEvent.OnFilterChange(
                                            PeopleFilterType.STUDENTS
                                        )
                                    )
                                },
                                label = {
                                    Text(
                                        stringResource(id = Strings.students)
                                    )
                                },
                                leadingIcon = if (viewModel.uiState.filter == PeopleFilterType.STUDENTS) {
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
                            AnimatedErrorScreen(
                                url = Constants.PEOPLE_SCREEN_EMPTY_ANIM_URL,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                errorMessage = dataState.message.asString()
                            )
                        } else {
                            val bottomMargin = if (isTeacher) 88.dp else 0.dp
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = scrollState,
                                contentPadding = PaddingValues(bottom = bottomMargin),
                                content = {
                                    items(viewModel.uiState.peoples, key = { it.id }) { user ->
                                        PeopleListItem(
                                            userProfile = user,
                                            modifier = Modifier.animateItemPlacement(),
                                            currentUserId = viewModel.uiState.currentUser?.uid.orEmpty(),
                                            isTeacher = isTeacher,
                                            courseOwnerId = course.ownerId,
                                            onLeaveClassClick = {
                                                viewModel.onEvent(
                                                    PeopleUiEvent.OnOpenLeaveClassDialogChange(
                                                        true
                                                    )
                                                )
                                            },
                                            onEmailClick = {
                                                composeEmail(
                                                    context,
                                                    arrayOf(user.emailAddress.orEmpty())
                                                )
                                            },
                                            onRemoveClick = {
                                                viewModel.onEvent(
                                                    PeopleUiEvent.OnOpenRemoveUserDialogChange(
                                                        user
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            if (isTeacher) {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(id = Strings.invite)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.PersonAddAlt,
                            contentDescription = "Invite"
                        )
                    },
                    onClick = { viewModel.onEvent(PeopleUiEvent.OnOpenFabMenuChange(true)) },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd),
                    expanded = expandedFab
                )
            }

            PullRefreshIndicator(
                viewModel.uiState.refreshing,
                refreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }

    if (viewModel.uiState.openFabMenu) {
        val bottomMargin =
            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 10.dp

        ModalBottomSheet(
            onDismissRequest = {
                viewModel.onEvent(PeopleUiEvent.OnOpenFabMenuChange(false))
            },
            sheetState = bottomSheetState,
            windowInsets = WindowInsets(0)
        ) {
            val courseLink = course.alternateLink
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
            Spacer(modifier = Modifier.height(bottomMargin))
        }
    }

    LeaveClassDialog(
        onDismissRequest = {
            viewModel.onEvent(PeopleUiEvent.OnOpenLeaveClassDialogChange(false))
        },
        openDialog = viewModel.uiState.openLeaveClassDialog,
        onConfirmClick = {
            viewModel.uiState.currentUser?.uid?.let {
                viewModel.onEvent(PeopleUiEvent.OnLeaveClass(it))
            }
        }
    )

    RemoveUserDialog(
        onDismissRequest = {
            viewModel.onEvent(PeopleUiEvent.OnOpenRemoveUserDialogChange(null))
        },
        userProfile = viewModel.uiState.removeUserProfile,
        userType = if (isTeacher) {
            UserType.TEACHER
        } else {
            UserType.STUDENT
        },
        onConfirmClick = { userType, uid ->
            viewModel.onEvent(
                PeopleUiEvent.OnRemoveUser(
                    userType,
                    uid
                )
            )
        }
    )

    ProgressDialog(openDialog = viewModel.uiState.openProgressDialog)
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