package app.edumate.presentation.people

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import app.edumate.R
import app.edumate.core.Constants
import app.edumate.core.Result
import app.edumate.core.utils.ClipboardUtils
import app.edumate.core.utils.IntentUtils
import app.edumate.domain.model.courses.Course
import app.edumate.domain.model.users.UserRole
import app.edumate.presentation.components.AnimatedErrorScreen
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LeaveCourseDialog
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.components.ProgressDialog
import app.edumate.presentation.people.components.DeletePersonDialog
import app.edumate.presentation.people.components.InviteBottomSheet
import app.edumate.presentation.people.components.PeopleListItem
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PeopleScreen(
    course: Course,
    onNavigateUp: () -> Unit,
    onLeaveCourseComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PeopleViewModel = koinViewModel(),
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnLeaveCourseComplete by rememberUpdatedState(onLeaveCourseComplete)

    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { viewModel.uiState }
            .filter { it.isUserLeftCourse }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnLeaveCourseComplete()
            }
    }

    PeopleContent(
        uiState = viewModel.uiState,
        onEvent = viewModel::onEvent,
        course = course,
        onNavigateUp = onNavigateUp,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleContent(
    uiState: PeopleUiState,
    onEvent: (PeopleUiEvent) -> Unit,
    course: Course,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomMargin = if (uiState.isCurrentUserTeacher) 100.dp else 0.dp
    val expandedFab by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex == 0
        }
    }
    val currentUserRole =
        when {
            course.ownerId == uiState.currentUserId -> CurrentUserRole.OWNER
            uiState.isCurrentUserTeacher -> CurrentUserRole.TEACHER
            else -> CurrentUserRole.STUDENT
        }

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(PeopleUiEvent.UserMessageShown)
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = course.name.orEmpty(),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                        IconButton(
                            onClick = {
                                onEvent(PeopleUiEvent.OnExpandedAppBarDropdownChange(true))
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                            )
                        }
                        DropdownMenu(
                            expanded = uiState.expandedAppBarDropdown,
                            onDismissRequest = {
                                onEvent(PeopleUiEvent.OnExpandedAppBarDropdownChange(false))
                            },
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = R.string.refresh))
                                },
                                onClick = {
                                    onEvent(PeopleUiEvent.OnExpandedAppBarDropdownChange(false))
                                    onEvent(PeopleUiEvent.OnRefresh)
                                },
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            if (uiState.isCurrentUserTeacher) {
                ExtendedFloatingActionButton(
                    text = {
                        Text(text = stringResource(id = R.string.invite))
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.PersonAddAlt,
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        onEvent(PeopleUiEvent.OnShowInviteBottomSheetChange(true))
                    },
                    expanded = expandedFab,
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = {
                onEvent(PeopleUiEvent.OnRefresh)
            },
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            when (val peopleResult = uiState.peopleResult) {
                is Result.Empty -> {}

                is Result.Error -> {
                    ErrorScreen(
                        onRetryClick = {
                            onEvent(PeopleUiEvent.OnRetry)
                        },
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = peopleResult.message!!.asString(),
                    )
                }

                is Result.Loading -> {
                    LoadingScreen()
                }

                is Result.Success -> {
                    Column {
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
                                    onEvent(PeopleUiEvent.OnFilterChange(PeopleFilterType.ALL))
                                },
                                label = {
                                    Text(stringResource(id = R.string.all))
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
                                    onEvent(PeopleUiEvent.OnFilterChange(PeopleFilterType.TEACHERS))
                                },
                                label = {
                                    Text(stringResource(id = R.string.teachers))
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
                                    onEvent(PeopleUiEvent.OnFilterChange(PeopleFilterType.STUDENTS))
                                },
                                label = {
                                    Text(stringResource(id = R.string.students))
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
                        if (uiState.filter == PeopleFilterType.STUDENTS && uiState.students.isEmpty()) {
                            AnimatedErrorScreen(
                                url = Constants.Lottie.ANIM_PEOPLE_SCREEN_EMPTY,
                                modifier = Modifier.fillMaxSize(),
                                errorMessage = stringResource(id = R.string.invite_students_to_your_class),
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = scrollState,
                                contentPadding = PaddingValues(bottom = bottomMargin),
                                content = {
                                    if (uiState.filter == PeopleFilterType.ALL || uiState.filter == PeopleFilterType.TEACHERS) {
                                        items(
                                            items = uiState.teachers,
                                            key = { it.user!!.id!! },
                                        ) { teacher ->
                                            PeopleListItem(
                                                user = teacher.user,
                                                role = teacher.role!!,
                                                courseOwnerId = course.ownerId.orEmpty(),
                                                currentUserId = uiState.currentUserId.orEmpty(),
                                                currentUserRole = currentUserRole,
                                                onEmailUserClick = { email ->
                                                    IntentUtils.composeEmail(
                                                        context,
                                                        arrayOf(email),
                                                    )
                                                },
                                                onLeaveClassClick = {
                                                    onEvent(
                                                        PeopleUiEvent.OnOpenLeaveClassDialogChange(
                                                            true,
                                                        ),
                                                    )
                                                },
                                                onRemoveUserClick = {
                                                    onEvent(
                                                        PeopleUiEvent.OnOpenDeleteUserDialogChange(
                                                            teacher,
                                                        ),
                                                    )
                                                },
                                                modifier = Modifier.animateItem(),
                                            )
                                        }
                                    }
                                    if (uiState.filter == PeopleFilterType.ALL || uiState.filter == PeopleFilterType.STUDENTS) {
                                        items(
                                            items = uiState.students,
                                            key = { it.user!!.id!! },
                                        ) { student ->
                                            PeopleListItem(
                                                user = student.user,
                                                role = student.role!!,
                                                courseOwnerId = course.ownerId.orEmpty(),
                                                currentUserId = uiState.currentUserId.orEmpty(),
                                                currentUserRole = currentUserRole,
                                                onEmailUserClick = { email ->
                                                    IntentUtils.composeEmail(
                                                        context,
                                                        arrayOf(email),
                                                    )
                                                },
                                                onLeaveClassClick = {
                                                    onEvent(
                                                        PeopleUiEvent.OnOpenLeaveClassDialogChange(
                                                            true,
                                                        ),
                                                    )
                                                },
                                                onRemoveUserClick = {
                                                    onEvent(
                                                        PeopleUiEvent.OnOpenDeleteUserDialogChange(
                                                            student,
                                                        ),
                                                    )
                                                },
                                                modifier = Modifier.animateItem(),
                                            )
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    InviteBottomSheet(
        show = uiState.showInviteBottomSheet,
        onDismissRequest = {
            onEvent(PeopleUiEvent.OnShowInviteBottomSheetChange(false))
        },
        onShareClick = {
            course.alternateLink?.let {
                IntentUtils.shareText(context, it)
            }
        },
        onCopyClick = {
            course.alternateLink?.let {
                ClipboardUtils.copyTextToClipboard(context, it) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.invite_link_copied))
                    }
                }
            }
        },
    )

    LeaveCourseDialog(
        open = uiState.openLeaveClassDialog,
        name = course.name,
        onDismissRequest = {
            onEvent(PeopleUiEvent.OnOpenLeaveClassDialogChange(false))
        },
        onConfirmButtonClick = {
            onEvent(PeopleUiEvent.OnOpenLeaveClassDialogChange(false))
            uiState.currentUserId?.let {
                onEvent(PeopleUiEvent.OnLeaveClass(it))
            }
        },
    )

    DeletePersonDialog(
        user = uiState.deletePerson?.user,
        isTeacher = uiState.deletePerson?.role == UserRole.TEACHER,
        onDismissRequest = {
            onEvent(PeopleUiEvent.OnOpenDeleteUserDialogChange(null))
        },
        onConfirmButtonClick = { userId ->
            onEvent(PeopleUiEvent.OnDeletePerson(userId))
        },
    )

    ProgressDialog(
        openDialog = uiState.openProgressDialog,
        onDismissRequest = {},
    )
}
