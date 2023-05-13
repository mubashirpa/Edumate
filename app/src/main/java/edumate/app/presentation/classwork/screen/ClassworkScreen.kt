package edumate.app.presentation.classwork.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.LiveHelp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.core.Constants
import edumate.app.core.DataState
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.domain.model.courses.Course
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.classwork.ClassworkUiEvent
import edumate.app.presentation.classwork.ClassworkUiState
import edumate.app.presentation.classwork.screen.components.ClassworkListItem
import edumate.app.presentation.classwork.screen.components.DeleteClassworkDialog
import edumate.app.presentation.components.AnimatedErrorScreen
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.ProgressDialog

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun ClassworkScreen(
    uiState: ClassworkUiState,
    onEvent: (ClassworkUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    course: Course,
    navigateToCreateClasswork: (courseId: String, workType: CourseWorkType) -> Unit,
    navigateToEditClasswork: (courseId: String, id: String, workType: CourseWorkType) -> Unit,
    navigateToViewClasswork: (
        courseId: String,
        id: String,
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
        onRefresh = { onEvent(ClassworkUiEvent.OnRefresh) }
    )
    val currentUserType =
        if (course.teacherGroupId.contains(uiState.currentUser?.uid)) {
            UserType.TEACHER
        } else {
            UserType.STUDENT
        }
    val expandedFab by remember { derivedStateOf { scrollState.firstVisibleItemIndex == 0 } }

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(ClassworkUiEvent.UserMessageShown)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = course.name) },
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
                        onClick = { onEvent(ClassworkUiEvent.OnAppBarMenuExpandedChange(true)) }
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                    DropdownMenu(
                        expanded = uiState.appBarMenuExpanded,
                        onDismissRequest = {
                            onEvent(ClassworkUiEvent.OnAppBarMenuExpandedChange(false))
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = Strings.refresh)) },
                            onClick = {
                                onEvent(ClassworkUiEvent.OnAppBarMenuExpandedChange(false))
                                onEvent(ClassworkUiEvent.OnRefresh)
                            }
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(refreshState)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            when (val dataState = uiState.dataState) {
                is DataState.EMPTY -> {
                    AnimatedErrorScreen(
                        url = Constants.CLASSWORK_SCREEN_EMPTY_ANIM_URL,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
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

                is DataState.ERROR -> {
                    ErrorScreen(
                        onRetryClick = { onEvent(ClassworkUiEvent.OnRetry) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        errorMessage = dataState.message.asString()
                    )
                }

                DataState.LOADING -> {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }

                DataState.SUCCESS -> {
                    val bottomMargin = if (currentUserType == UserType.TEACHER) 88.dp else 0.dp
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = scrollState,
                        contentPadding = PaddingValues(bottom = bottomMargin),
                        content = {
                            items(uiState.classwork, key = { it.id }) { classwork ->
                                val id = classwork.id
                                val type = classwork.workType
                                ClassworkListItem(
                                    work = classwork,
                                    modifier = Modifier.animateItemPlacement(),
                                    currentUserType = currentUserType,
                                    workType = type,
                                    onClick = {
                                        navigateToViewClasswork(
                                            courseId,
                                            id,
                                            type,
                                            currentUserType
                                        )
                                    },
                                    onEditClick = {
                                        navigateToEditClasswork(courseId, id, type)
                                    },
                                    onDeleteClick = {
                                        onEvent(
                                            ClassworkUiEvent.OnOpenDeleteClassworkDialogChange(
                                                classwork
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    )
                }

                DataState.UNKNOWN -> {}
            }

            if (currentUserType == UserType.TEACHER) {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(id = Strings.create)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create classwork"
                        )
                    },
                    onClick = { onEvent(ClassworkUiEvent.OnOpenFabMenuChange(true)) },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd),
                    expanded = expandedFab
                )
            }

            PullRefreshIndicator(
                uiState.refreshing,
                refreshState,
                Modifier.align(Alignment.TopCenter)
            )
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

    DeleteClassworkDialog(
        onDismissRequest = {
            onEvent(ClassworkUiEvent.OnOpenDeleteClassworkDialogChange(null))
        },
        classwork = uiState.deleteClasswork,
        onConfirmClick = {
            onEvent(ClassworkUiEvent.OnDeleteClasswork(it))
        }
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
}