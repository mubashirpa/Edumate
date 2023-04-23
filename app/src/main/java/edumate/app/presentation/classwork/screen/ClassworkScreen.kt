package edumate.app.presentation.classwork.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.LiveHelp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.core.DataState
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.domain.model.courses.Course
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.class_details.screen.components.ClassDetailsAppBar
import edumate.app.presentation.classwork.ClassworkUiEvent
import edumate.app.presentation.classwork.ClassworkUiState
import edumate.app.presentation.classwork.screen.components.ClassworkListItem
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.ProgressDialog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ClassworkScreen(
    uiState: ClassworkUiState,
    onEvent: (ClassworkUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    course: Course,
    navigateToCreateClasswork: (courseId: String, workType: CourseWorkType) -> Unit,
    navigateToEditClasswork: (courseId: String, classworkId: String, workType: CourseWorkType) -> Unit,
    navigateToViewClasswork: (
        courseId: String,
        classworkId: String,
        workType: CourseWorkType,
        currentUserType: UserType
    ) -> Unit,
    onBackPressed: () -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val courseId = course.id
    val scrollState = rememberLazyListState()
    val refreshState = rememberPullRefreshState(
        refreshing = uiState.refreshing,
        onRefresh = {
            onEvent(ClassworkUiEvent.OnRefresh)
        }
    )
    val currentUserType =
        if (course.teachers.contains(uiState.currentUser?.uid)) {
            UserType.TEACHER
        } else {
            UserType.STUDENT
        }
    val jumpToBottomButtonEnabled by remember {
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
        ClassDetailsAppBar(
            title = course.name,
            scrollBehavior = scrollBehavior,
            onNavigationClick = onBackPressed
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            when (val dataState = uiState.dataState) {
                is DataState.UNKNOWN -> {
                    // Nothing happened
                }

                is DataState.LOADING -> {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }

                is DataState.ERROR -> {
                    ErrorScreen(
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = dataState.message.asString(),
                        onRetry = {
                            onEvent(ClassworkUiEvent.OnRetry)
                        }
                    )
                }

                is DataState.EMPTY -> {
                    ErrorScreen(
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = if (currentUserType == UserType.TEACHER) {
                            stringResource(
                                id = Strings.add_assignments_and_other_works_for_class
                            )
                        } else {
                            stringResource(
                                id = Strings.your_teacher_hasnt_assigned_any_classwork_yet
                            )
                        }
                    )
                }

                is DataState.SUCCESS -> {
                    Box(
                        modifier = Modifier
                            .pullRefresh(refreshState)
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = scrollState,
                            content = {
                                items(uiState.classwork) { classwork ->
                                    val id = classwork.id
                                    val type = classwork.workType
                                    ClassworkListItem(
                                        work = classwork,
                                        currentUserType = currentUserType,
                                        workType = type,
                                        onEdit = {
                                            navigateToEditClasswork(courseId, id, type)
                                        },
                                        onDelete = {
                                            onEvent(
                                                ClassworkUiEvent.OnOpenDeleteClassworkDialogChange(
                                                    classwork
                                                )
                                            )
                                        },
                                        onClick = {
                                            navigateToViewClasswork(
                                                courseId,
                                                id,
                                                type,
                                                currentUserType
                                            )
                                        }
                                    )
                                }
                            }
                        )

                        PullRefreshIndicator(
                            uiState.refreshing,
                            refreshState,
                            Modifier.align(Alignment.TopCenter)
                        )
                    }
                }
            }

            if (currentUserType == UserType.TEACHER) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd),
                    horizontalArrangement = Arrangement.End
                ) {
                    AnimatedVisibility(visible = jumpToBottomButtonEnabled) {
                        FloatingActionButton(
                            onClick = {
                                onEvent(ClassworkUiEvent.OnOpenFabMenuChange(true))
                            },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(id = Strings.create_classwork)
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.openFabMenu) {
        ModalBottomSheet(
            onDismissRequest = {
                onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
            },
            sheetState = bottomSheetState
        ) {
            ListItem(
                headlineContent = { Text(text = stringResource(id = Strings.assignment)) },
                modifier = Modifier.clickable {
                    onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
                    navigateToCreateClasswork(
                        courseId,
                        CourseWorkType.ASSIGNMENT
                    )
                },
                leadingContent = {
                    Icon(imageVector = Icons.Outlined.Assignment, contentDescription = null)
                }
            )
            ListItem(
                headlineContent = { Text(text = stringResource(id = Strings.question)) },
                modifier = Modifier.clickable {
                    onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
                    navigateToCreateClasswork(
                        courseId,
                        CourseWorkType.SHORT_ANSWER_QUESTION
                    )
                },
                leadingContent = {
                    Icon(imageVector = Icons.Outlined.LiveHelp, contentDescription = null)
                }
            )
            ListItem(
                headlineContent = { Text(text = stringResource(id = Strings.material)) },
                modifier = Modifier.clickable {
                    onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
                    navigateToCreateClasswork(
                        courseId,
                        CourseWorkType.MATERIAL
                    )
                },
                leadingContent = {
                    Icon(imageVector = Icons.Outlined.Book, contentDescription = null)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    ProgressDialog(openDialog = uiState.openProgressDialog)

    DeleteClassworkDialog(
        onDismissRequest = {
            onEvent(ClassworkUiEvent.OnOpenDeleteClassworkDialogChange(null))
        },
        classwork = uiState.deleteClasswork,
        onConfirmClick = {
            onEvent(ClassworkUiEvent.OnDeleteClasswork(it))
        }
    )
}

@Composable
private fun DeleteClassworkDialog(
    onDismissRequest: () -> Unit,
    classwork: CourseWork?,
    onConfirmClick: (workId: String) -> Unit
) {
    if (classwork != null) {
        val title = when (classwork.workType) {
            CourseWorkType.MATERIAL -> stringResource(id = Strings.delete_material)
            CourseWorkType.ASSIGNMENT -> stringResource(id = Strings.delete_assignment)
            CourseWorkType.SHORT_ANSWER_QUESTION -> stringResource(id = Strings.delete_question)
            CourseWorkType.MULTIPLE_CHOICE_QUESTION -> stringResource(id = Strings.delete_question)
            else -> stringResource(id = Strings.delete)
        }
        val message = when (classwork.workType) {
            CourseWorkType.MATERIAL -> stringResource(id = Strings.comments_will_also_be_deleted)
            CourseWorkType.ASSIGNMENT -> stringResource(
                id = Strings.marks_and_comments_will_also_be_deleted
            )

            CourseWorkType.SHORT_ANSWER_QUESTION -> stringResource(
                id = Strings.marks_and_comments_will_also_be_deleted
            )

            CourseWorkType.MULTIPLE_CHOICE_QUESTION -> stringResource(
                id = Strings.marks_and_comments_will_also_be_deleted
            )

            else -> ""
        }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = title)
            },
            text = {
                Text(text = message)
            },
            confirmButton = {
                TextButton(onClick = { onConfirmClick(classwork.id) }) {
                    Text(stringResource(id = Strings.delete))
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