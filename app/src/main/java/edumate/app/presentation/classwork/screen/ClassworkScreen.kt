package edumate.app.presentation.classwork.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edumate.app.R.string as Strings
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.domain.model.courses.Course
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.class_details.screen.components.ClassDetailsAppBar
import edumate.app.presentation.classwork.ClassworkUiEvent
import edumate.app.presentation.classwork.ClassworkViewModel
import edumate.app.presentation.classwork.DataState
import edumate.app.presentation.classwork.screen.components.ClassworkListItem
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ClassworkScreen(
    viewModel: ClassworkViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    course: Course,
    navigateToCreateClasswork: (courseId: String, courseName: String, workType: CourseWorkType) -> Unit,
    navigateToViewClasswork: () -> Unit,
    onBackPressed: () -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val context = LocalContext.current
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.uiState.refreshing,
        onRefresh = {
            viewModel.onEvent(ClassworkUiEvent.OnRefresh)
        }
    )
    val currentUserType =
        if (course.teachers?.contains(viewModel.uiState.currentUser?.uid) == true) {
            UserType.TEACHER
        } else {
            UserType.STUDENT
        }

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(ClassworkUiEvent.UserMessageShown)
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
                            viewModel.onEvent(ClassworkUiEvent.OnRetry)
                        }
                    )
                }
                is DataState.EMPTY -> {
                    ErrorScreen(
                        errorMessage = if (currentUserType == UserType.TEACHER) {
                            stringResource(id = Strings.add_assignments_and_other_works_for_class)
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
                            content = {
                                items(viewModel.uiState.classWorks) { classWork ->
                                    ClassworkListItem(
                                        work = classWork,
                                        currentUserType = currentUserType,
                                        workType = classWork.workType,
                                        onEdit = {
                                            // TODO("Not yet implemented")
                                        },
                                        onDelete = {
                                            viewModel.onEvent(
                                                ClassworkUiEvent.OnDelete(
                                                    classWork.id,
                                                    classWork.courseId
                                                )
                                            )
                                        },
                                        onClick = navigateToViewClasswork
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

            if (currentUserType == UserType.TEACHER) {
                FloatingActionButton(
                    onClick = {
                        viewModel.onEvent(ClassworkUiEvent.OnOpenFabMenuChange(true))
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .imePadding()
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = Strings.create_classwork)
                    )
                }
            }
        }
    }

    if (viewModel.uiState.openFabMenu) {
        val bottomMargin =
            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 10.dp

        ModalBottomSheet(
            onDismissRequest = {
                viewModel.onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
            }
        ) {
            ListItem(
                headlineContent = { Text(text = stringResource(id = Strings.assignment)) },
                modifier = Modifier.clickable {
                    viewModel.onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
                    navigateToCreateClasswork(
                        course.id.orEmpty(),
                        course.name,
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
                    viewModel.onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
                    navigateToCreateClasswork(
                        course.id.orEmpty(),
                        course.name,
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
                    viewModel.onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
                    navigateToCreateClasswork(
                        course.id.orEmpty(),
                        course.name,
                        CourseWorkType.MATERIAL
                    )
                },
                leadingContent = {
                    Icon(imageVector = Icons.Outlined.Book, contentDescription = null)
                }
            )
            Spacer(modifier = Modifier.height(bottomMargin))
        }
    }
}