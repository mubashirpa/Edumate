package edumate.app.presentation.people

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import edumate.app.core.Constants
import edumate.app.core.Result
import edumate.app.core.utils.IntentUtils
import edumate.app.domain.model.classroom.courses.Course
import edumate.app.presentation.components.AnimatedErrorScreen
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LeaveCourseDialog
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.people.components.DeleteUserDialog
import edumate.app.presentation.people.components.InviteBottomSheet
import edumate.app.presentation.people.components.PeopleListItem
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import edumate.app.R.string as Strings

@Composable
fun PeopleScreen(
    viewModel: PeopleViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    course: Course,
    onLeaveClass: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnLeaveClass by rememberUpdatedState(onLeaveClass)

    LaunchedEffect(viewModel, lifecycle) {
        // Whenever the uiState changes, check if the user is leave class and
        // call the `onLeaveClass` event when `lifecycle` is at least STARTED
        snapshotFlow { viewModel.uiState }
            .filter { it.isUserLeaveClass }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnLeaveClass()
            }
    }

    PeopleScreenContent(
        uiState = viewModel.uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        course = course,
        onBackPressed = onBackPressed,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class,
)
@Composable
private fun PeopleScreenContent(
    uiState: PeopleUiState,
    onEvent: (PeopleUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    course: Course,
    onBackPressed: () -> Unit,
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topBarState)
    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isRefreshing = uiState.isStudentsRefreshing || uiState.isTeachersRefreshing
    val refreshState =
        rememberPullRefreshState(
            refreshing = isRefreshing,
            onRefresh = {
                onEvent(PeopleUiEvent.OnRefresh)
            },
        )
    val isTeacher = course.teachers?.any { it.userId == uiState.userId } == true
    val expandedFab by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex == 0
        }
    }

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(PeopleUiEvent.UserMessageShown)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = course.name.orEmpty(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
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
                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                    IconButton(
                        onClick = {
                            onEvent(PeopleUiEvent.OnAppBarDropdownExpandedChange(true))
                        },
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null,
                        )
                    }
                    DropdownMenu(
                        expanded = uiState.appBarDropdownExpanded,
                        onDismissRequest = {
                            onEvent(PeopleUiEvent.OnAppBarDropdownExpandedChange(false))
                        },
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(id = Strings.refresh))
                            },
                            onClick = {
                                onEvent(PeopleUiEvent.OnAppBarDropdownExpandedChange(false))
                                onEvent(PeopleUiEvent.OnRefresh)
                            },
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior,
        )
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .pullRefresh(refreshState)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            val studentsResult = uiState.studentsResult
            val teachersResult = uiState.teachersResult

            when {
                studentsResult is Result.Loading || teachersResult is Result.Loading -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center),
                    ) {
                        CircularProgressIndicator()
                    }
                }

                studentsResult is Result.Success && teachersResult is Result.Success -> {
                    val bottomMargin = if (isTeacher) 88.dp else 0.dp
                    val students = studentsResult.data.orEmpty()

                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            FilterChip(
                                selected = uiState.filter == PeopleFilterType.ALL,
                                onClick = {
                                    onEvent(
                                        PeopleUiEvent.OnFilterChange(
                                            PeopleFilterType.ALL,
                                        ),
                                    )
                                },
                                label = {
                                    Text(stringResource(id = Strings.all))
                                },
                                leadingIcon =
                                    if (uiState.filter == PeopleFilterType.ALL) {
                                        {
                                            Icon(
                                                imageVector = Icons.Filled.Done,
                                                contentDescription = null,
                                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                                            )
                                        }
                                    } else {
                                        null
                                    },
                            )
                            FilterChip(
                                selected = uiState.filter == PeopleFilterType.TEACHERS,
                                onClick = {
                                    onEvent(
                                        PeopleUiEvent.OnFilterChange(
                                            PeopleFilterType.TEACHERS,
                                        ),
                                    )
                                },
                                label = {
                                    Text(stringResource(id = Strings.teachers))
                                },
                                leadingIcon =
                                    if (uiState.filter == PeopleFilterType.TEACHERS) {
                                        {
                                            Icon(
                                                imageVector = Icons.Filled.Done,
                                                contentDescription = null,
                                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                                            )
                                        }
                                    } else {
                                        null
                                    },
                            )
                            FilterChip(
                                selected = uiState.filter == PeopleFilterType.STUDENTS,
                                onClick = {
                                    onEvent(
                                        PeopleUiEvent.OnFilterChange(
                                            PeopleFilterType.STUDENTS,
                                        ),
                                    )
                                },
                                label = {
                                    Text(stringResource(id = Strings.students))
                                },
                                leadingIcon =
                                    if (uiState.filter == PeopleFilterType.STUDENTS) {
                                        {
                                            Icon(
                                                imageVector = Icons.Filled.Done,
                                                contentDescription = null,
                                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                                            )
                                        }
                                    } else {
                                        null
                                    },
                            )
                        }
                        if (uiState.filter == PeopleFilterType.STUDENTS && students.isEmpty()) {
                            AnimatedErrorScreen(
                                url = Constants.ANIM_PEOPLE_SCREEN_EMPTY,
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                errorMessage = stringResource(id = Strings.invite_students_to_your_class),
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = scrollState,
                                contentPadding = PaddingValues(bottom = bottomMargin),
                                content = {
                                    if (uiState.filter == PeopleFilterType.ALL || uiState.filter == PeopleFilterType.TEACHERS) {
                                        items(
                                            items = teachersResult.data.orEmpty(),
                                            // key = { it.userId!! }, // TODO("Fix in server")
                                        ) { teacher ->
                                            PeopleListItem(
                                                profile = teacher.profile,
                                                course = course,
                                                userId = uiState.userId.orEmpty(),
                                                modifier = Modifier.animateItemPlacement(),
                                                onEmailClick = {
                                                    val email = teacher.profile?.emailAddress
                                                    email?.let {
                                                        IntentUtils.composeEmail(
                                                            context,
                                                            arrayOf(it),
                                                        )
                                                    }
                                                },
                                                onLeaveClassClick = {
                                                    onEvent(
                                                        PeopleUiEvent.OnOpenLeaveClassDialogChange(
                                                            true,
                                                        ),
                                                    )
                                                },
                                                onMakeClassOwnerClick = { /*TODO*/ },
                                                onRemoveClick = {
                                                    onEvent(
                                                        PeopleUiEvent.OnOpenDeleteUserDialogChange(
                                                            it,
                                                        ),
                                                    )
                                                },
                                            )
                                        }
                                    }
                                    if (uiState.filter == PeopleFilterType.ALL || uiState.filter == PeopleFilterType.STUDENTS) {
                                        items(
                                            items = students,
                                            // key = { it.userId!! }, // TODO("Fix in server")
                                        ) { student ->
                                            PeopleListItem(
                                                profile = student.profile,
                                                course = course,
                                                userId = uiState.userId.orEmpty(),
                                                modifier = Modifier.animateItemPlacement(),
                                                onEmailClick = {
                                                    val email = student.profile?.emailAddress
                                                    email?.let {
                                                        IntentUtils.composeEmail(
                                                            context,
                                                            arrayOf(it),
                                                        )
                                                    }
                                                },
                                                onLeaveClassClick = {
                                                    onEvent(
                                                        PeopleUiEvent.OnOpenLeaveClassDialogChange(
                                                            true,
                                                        ),
                                                    )
                                                },
                                                onMakeClassOwnerClick = { /*TODO*/ },
                                                onRemoveClick = {
                                                    onEvent(
                                                        PeopleUiEvent.OnOpenDeleteUserDialogChange(
                                                            it,
                                                        ),
                                                    )
                                                },
                                            )
                                        }
                                    }
                                },
                            )
                        }
                    }
                }

                studentsResult is Result.Error || teachersResult is Result.Error -> {
                    ErrorScreen(
                        onRetryClick = {
                            onEvent(PeopleUiEvent.OnRetry)
                        },
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                        errorMessage = stringResource(id = Strings.cannot_retrieve_peoples_at_this_time_please_try_again_later),
                    )
                }
            }

            if (isTeacher) {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(id = Strings.invite)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.PersonAddAlt,
                            contentDescription = "Invite",
                        )
                    },
                    onClick = {
                        onEvent(PeopleUiEvent.OnShowInviteBottomSheetChange(true))
                    },
                    modifier =
                        Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomEnd),
                    expanded = expandedFab,
                )
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }

    InviteBottomSheet(
        onDismissRequest = {
            onEvent(PeopleUiEvent.OnShowInviteBottomSheetChange(false))
        },
        show = uiState.showInviteBottomSheet,
        onShareClick = {
            course.alternateLink?.let {
                IntentUtils.shareText(context, it)
            }
        },
        onCopyClick = {
            course.alternateLink?.let {
                copy(context, it) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(context.getString(Strings.copied_invitation_link))
                    }
                }
            }
        },
    )

    LeaveCourseDialog(
        onDismissRequest = {
            onEvent(PeopleUiEvent.OnOpenLeaveClassDialogChange(false))
        },
        open = uiState.openLeaveClassDialog,
        name = course.name.orEmpty(),
        onConfirmButtonClick = {
            uiState.userId?.let {
                onEvent(PeopleUiEvent.OnLeaveClass(it))
            }
        },
    )

    DeleteUserDialog(
        onDismissRequest = {
            onEvent(PeopleUiEvent.OnOpenDeleteUserDialogChange(null))
        },
        open = uiState.deleteUserProfile != null,
        userProfile = uiState.deleteUserProfile,
        isTeacher = course.teachers?.any { it.userId == uiState.deleteUserProfile?.id } == true,
        onConfirmButtonClick = { userId, teacher ->
            if (teacher) {
                onEvent(PeopleUiEvent.OnDeleteTeacher(userId))
            } else {
                onEvent(PeopleUiEvent.OnDeleteStudent(userId))
            }
        },
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
}

private fun copy(
    context: Context,
    textCopied: String,
    onSuccess: () -> Unit,
) {
    val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.setPrimaryClip(ClipData.newPlainText(textCopied, textCopied))
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        onSuccess()
    }
}
