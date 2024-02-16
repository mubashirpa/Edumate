package edumate.app.presentation.classwork

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edumate.app.core.Constants
import edumate.app.core.Result
import edumate.app.domain.model.classroom.courseWork.CourseWork
import edumate.app.domain.model.classroom.courseWork.CourseWorkType
import edumate.app.domain.model.classroom.courses.Course
import edumate.app.presentation.classwork.components.CourseWorkListItem
import edumate.app.presentation.classwork.components.CreateCourseWorkBottomSheet
import edumate.app.presentation.classwork.components.DeleteCourseWorkDialog
import edumate.app.presentation.components.AnimatedErrorScreen
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.ProgressDialog
import edumate.app.R.string as Strings

// TODO("Implement Material")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ClassworkScreen(
    uiState: ClassworkUiState,
    onEvent: (ClassworkUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    course: Course,
    navigateToCreateClasswork: (courseId: String, workType: CourseWorkType?, id: String?) -> Unit,
    navigateToViewClasswork: (courseId: String, id: String, workType: CourseWorkType) -> Unit,
    onBackPressed: () -> Unit,
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val context = LocalContext.current
    val courseId = course.id.orEmpty()
    val scrollState = rememberLazyListState()
    val refreshState =
        rememberPullRefreshState(
            refreshing = uiState.refreshing,
            onRefresh = {
                onEvent(ClassworkUiEvent.OnRefresh)
            },
        )
    val isTeacher = true // TODO
    val expandedFab by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex == 0
        }
    }

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(ClassworkUiEvent.UserMessageShown)
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
                            onEvent(ClassworkUiEvent.OnAppBarMenuExpandedChange(true))
                        },
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null,
                        )
                    }
                    DropdownMenu(
                        expanded = uiState.appBarMenuExpanded,
                        onDismissRequest = {
                            onEvent(ClassworkUiEvent.OnAppBarMenuExpandedChange(false))
                        },
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(id = Strings.refresh))
                            },
                            onClick = {
                                onEvent(ClassworkUiEvent.OnAppBarMenuExpandedChange(false))
                                onEvent(ClassworkUiEvent.OnRefresh)
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
            when (val courseWorkResult = uiState.courseWorkResult) {
                is Result.Empty -> {
                    // Nothing is shown
                }

                is Result.Error -> {
                    ErrorScreen(
                        onRetryClick = { onEvent(ClassworkUiEvent.OnRetry) },
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                        errorMessage = courseWorkResult.message!!.asString(),
                    )
                }

                is Result.Loading -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center),
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is Result.Success -> {
                    val courseWork = courseWorkResult.data
                    if (courseWork.isNullOrEmpty()) {
                        AnimatedErrorScreen(
                            url = Constants.ANIM_CLASSWORK_SCREEN_EMPTY,
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                            errorMessage =
                                if (isTeacher) {
                                    stringResource(id = Strings.add_assignments_and_other_works_for_class)
                                } else {
                                    stringResource(id = Strings.your_teacher_hasnt_assigned_any_classwork_yet)
                                },
                        )
                    } else {
                        ClassworkScreenContent(
                            courseWork = courseWork,
                            scrollState = scrollState,
                            isTeacher = isTeacher,
                            onViewClick = { id, type ->
                                navigateToViewClasswork(courseId, id, type)
                            },
                            onEditClick = { id ->
                                navigateToCreateClasswork(courseId, null, id)
                            },
                            onDeleteClick = { work ->
                                onEvent(ClassworkUiEvent.OnOpenDeleteCourseWorkDialogChange(work))
                            },
                        )
                    }
                }
            }

            if (isTeacher) {
                ExtendedFloatingActionButton(
                    text = {
                        Text(text = stringResource(id = Strings.create))
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        onEvent(ClassworkUiEvent.OnShowCreateCourseWorkBottomSheetChange(true))
                    },
                    modifier =
                        Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomEnd),
                    expanded = expandedFab,
                )
            }

            PullRefreshIndicator(
                refreshing = uiState.refreshing,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }

    CreateCourseWorkBottomSheet(
        onDismissRequest = {
            onEvent(ClassworkUiEvent.OnShowCreateCourseWorkBottomSheetChange(false))
        },
        showBottomSheet = uiState.showCreateCourseWorkBottomSheet,
        onCreateCourseWork = { type ->
            navigateToCreateClasswork(courseId, type, null)
        },
        onCreateMaterial = { /*TODO*/ },
    )

    DeleteCourseWorkDialog(
        onDismissRequest = {
            onEvent(ClassworkUiEvent.OnOpenDeleteCourseWorkDialogChange(null))
        },
        openAlertDialog = uiState.deleteCourseWork != null,
        workType = uiState.deleteCourseWork?.workType,
        onConfirmClick = {
            uiState.deleteCourseWork?.id?.also { id ->
                onEvent(ClassworkUiEvent.OnDeleteCourseWork(id))
            }
        },
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ClassworkScreenContent(
    courseWork: List<CourseWork>,
    scrollState: LazyListState,
    isTeacher: Boolean,
    onViewClick: (id: String, workType: CourseWorkType) -> Unit,
    onEditClick: (id: String) -> Unit,
    onDeleteClick: (courseWork: CourseWork) -> Unit,
) {
    val bottomMargin = if (isTeacher) 88.dp else 0.dp
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        contentPadding = PaddingValues(bottom = bottomMargin),
        content = {
            items(
                items = courseWork,
                key = { it.id!! },
            ) { work ->
                val id = work.id
                val type = work.workType ?: CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED
                CourseWorkListItem(
                    courseWork = work,
                    modifier = Modifier.animateItemPlacement(),
                    isTeacher = isTeacher,
                    workType = type,
                    onClick = {
                        id?.also {
                            onViewClick(it, type)
                        }
                    },
                    onEditClick = {
                        id?.also {
                            onEditClick(it)
                        }
                    },
                    onDeleteClick = {
                        onDeleteClick(work)
                    },
                )
            }
        },
    )
}
