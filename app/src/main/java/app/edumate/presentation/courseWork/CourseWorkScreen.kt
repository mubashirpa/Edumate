package app.edumate.presentation.courseWork

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.Constants
import app.edumate.core.Result
import app.edumate.domain.model.course.CourseWithMembers
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.presentation.components.AnimatedErrorScreen
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.components.ProgressDialog
import app.edumate.presentation.courseDetails.CourseUserRole
import app.edumate.presentation.courseWork.components.CourseWorkListItem
import app.edumate.presentation.courseWork.components.CreateCourseWorkBottomSheet
import app.edumate.presentation.courseWork.components.DeleteCourseWorkDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseWorkScreen(
    uiState: CourseWorkUiState,
    onEvent: (CourseWorkUiEvent) -> Unit,
    courseWithMembers: CourseWithMembers,
    currentUserRole: CourseUserRole,
    onNavigateUp: () -> Unit,
    onNavigateToCreateClasswork: (workType: CourseWorkType, id: String?) -> Unit,
    onNavigateToViewClasswork: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val expandedFab by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex == 0
        }
    }
    val isCurrentUserTeacher = currentUserRole is CourseUserRole.Teacher

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(CourseWorkUiEvent.UserMessageShown)
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = courseWithMembers.name.orEmpty(),
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
                                onEvent(CourseWorkUiEvent.OnExpandedAppBarDropdownChange(true))
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
                                onEvent(CourseWorkUiEvent.OnExpandedAppBarDropdownChange(false))
                            },
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(id = R.string.refresh))
                                },
                                onClick = {
                                    onEvent(CourseWorkUiEvent.OnExpandedAppBarDropdownChange(false))
                                    onEvent(CourseWorkUiEvent.Refresh)
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
            if (isCurrentUserTeacher) {
                ExtendedFloatingActionButton(
                    text = {
                        Text(text = stringResource(id = R.string.create))
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        onEvent(CourseWorkUiEvent.OnShowCreateCourseWorkBottomSheetChange(true))
                    },
                    expanded = expandedFab,
                )
            }
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = {
                onEvent(CourseWorkUiEvent.Refresh)
            },
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            when (val courseWorkResult = uiState.courseWorkResult) {
                is Result.Empty -> {}

                is Result.Error -> {
                    ErrorScreen(
                        onRetryClick = {
                            onEvent(CourseWorkUiEvent.Retry)
                        },
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = courseWorkResult.message!!.asString(),
                    )
                }

                is Result.Loading -> {
                    LoadingScreen()
                }

                is Result.Success -> {
                    val courseWorks = courseWorkResult.data.orEmpty()
                    val bottomMargin = if (isCurrentUserTeacher) 100.dp else 0.dp

                    if (courseWorks.isEmpty()) {
                        AnimatedErrorScreen(
                            url = Constants.Lottie.ANIM_CLASSWORK_SCREEN_EMPTY,
                            modifier = Modifier.fillMaxSize(),
                            errorMessage =
                                if (isCurrentUserTeacher) {
                                    stringResource(id = R.string.add_assignments_and_other_works_for_class)
                                } else {
                                    stringResource(R.string.your_teacher_hasn_t_assigned_any_classwork_yet)
                                },
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = scrollState,
                            contentPadding = PaddingValues(bottom = bottomMargin),
                            content = {
                                items(
                                    items = courseWorks,
                                    key = { it.id!! },
                                ) { courseWork ->
                                    CourseWorkListItem(
                                        onClick = onNavigateToViewClasswork,
                                        courseWork = courseWork,
                                        workType = courseWork.workType!!,
                                        isCurrentUserTeacher = isCurrentUserTeacher,
                                        onEditClick = { id, workType ->
                                            onNavigateToCreateClasswork(workType, id)
                                        },
                                        onDeleteClick = {
                                            onEvent(
                                                CourseWorkUiEvent.OnOpenDeleteCourseWorkDialogChange(
                                                    it,
                                                ),
                                            )
                                        },
                                        modifier = Modifier.animateItem(),
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    CreateCourseWorkBottomSheet(
        onDismissRequest = {
            onEvent(CourseWorkUiEvent.OnShowCreateCourseWorkBottomSheetChange(false))
        },
        show = uiState.showCreateCourseWorkBottomSheet,
        onCreateCourseWork = { type ->
            onNavigateToCreateClasswork(type, null)
        },
    )

    DeleteCourseWorkDialog(
        onDismissRequest = {
            onEvent(CourseWorkUiEvent.OnOpenDeleteCourseWorkDialogChange(null))
        },
        workType = uiState.deleteCourseWork?.workType,
        onConfirmButtonClick = {
            uiState.deleteCourseWork?.id?.also { id ->
                onEvent(CourseWorkUiEvent.DeleteCourseWork(id))
            }
        },
    )

    ProgressDialog(
        open = uiState.openProgressDialog,
        onDismissRequest = {},
    )
}
