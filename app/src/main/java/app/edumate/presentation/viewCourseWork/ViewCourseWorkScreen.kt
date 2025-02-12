package app.edumate.presentation.viewCourseWork

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.utils.FileUtils
import app.edumate.core.utils.IntentUtils
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.presentation.components.CommentsBottomSheet
import app.edumate.presentation.components.CommentsBottomSheetUiEvent
import app.edumate.presentation.components.CommentsBottomSheetUiState
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.components.ProgressDialog
import app.edumate.presentation.courseDetails.CourseUserRole
import app.edumate.presentation.studentWork.StudentWorkScreen
import app.edumate.presentation.viewCourseWork.components.ViewCourseWorkContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewCourseWorkScreen(
    uiState: ViewCourseWorkUiState,
    onEvent: (ViewCourseWorkUiEvent) -> Unit,
    currentUserRole: CourseUserRole,
    commentsUiState: CommentsBottomSheetUiState,
    commentsOnEvent: (CommentsBottomSheetUiEvent) -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToImageViewer: (url: String, title: String?) -> Unit,
    onNavigateToViewStudentSubmission: (courseWorkId: String, studentId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    val fileUtils = remember { FileUtils(context) }
    val isCurrentUserTeacher = currentUserRole is CourseUserRole.Teacher
    val courseWorkResult = uiState.courseWorkResult
    val courseWork = courseWorkResult.data
    val isSingleTabScreen = !isCurrentUserTeacher || courseWork?.workType == CourseWorkType.MATERIAL
    val tabs =
        when (courseWork?.workType) {
            CourseWorkType.ASSIGNMENT -> {
                listOf(
                    ViewCourseWorkTabScreen.Instructions,
                    ViewCourseWorkTabScreen.StudentWork,
                )
            }

            CourseWorkType.SHORT_ANSWER_QUESTION,
            CourseWorkType.MULTIPLE_CHOICE_QUESTION,
            -> {
                listOf(
                    ViewCourseWorkTabScreen.Question,
                    ViewCourseWorkTabScreen.StudentAnswers,
                )
            }

            else -> emptyList()
        }
    val pagerState =
        rememberPagerState(initialPage = if (isSingleTabScreen) 0 else 1) {
            if (isSingleTabScreen) 1 else tabs.size
        }
    val isFabVisible =
        !isCurrentUserTeacher &&
            courseWork?.workType != null &&
            courseWork.workType != CourseWorkType.MATERIAL

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(ViewCourseWorkUiEvent.UserMessageShown)
        }
    }

    Scaffold(
        modifier =
            if (isSingleTabScreen) {
                modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            } else {
                modifier
            },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    if (isCurrentUserTeacher) {
                        courseWork?.alternateLink?.let { alternateLink ->
                            IconButton(
                                onClick = {
                                    IntentUtils.shareText(context, alternateLink)
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                        IconButton(
                            onClick = {
                                onEvent(
                                    ViewCourseWorkUiEvent.OnExpandedAppBarDropdownChange(
                                        true,
                                    ),
                                )
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
                                onEvent(
                                    ViewCourseWorkUiEvent.OnExpandedAppBarDropdownChange(
                                        false,
                                    ),
                                )
                            },
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = R.string.refresh))
                                },
                                onClick = {
                                    onEvent(
                                        ViewCourseWorkUiEvent.OnExpandedAppBarDropdownChange(
                                            false,
                                        ),
                                    )
                                    onEvent(ViewCourseWorkUiEvent.Refresh)
                                },
                            )
                            // TODO("Add edit and delete for teachers")
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
            if (isFabVisible) {
                ExtendedFloatingActionButton(
                    text = {
                        Text(text = stringResource(R.string.comments))
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Comment,
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        onEvent(ViewCourseWorkUiEvent.OnShowCommentsBottomSheetChange(true))
                    },
                )
            }
        },
    ) { innerPadding ->
        when (courseWorkResult) {
            is Result.Empty -> {}

            is Result.Error -> {
                ErrorScreen(
                    onRetryClick = {
                        onEvent(ViewCourseWorkUiEvent.Retry)
                    },
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    errorMessage = courseWorkResult.message!!.asString(),
                )
            }

            is Result.Loading -> {
                LoadingScreen(modifier = Modifier.padding(innerPadding))
            }

            is Result.Success -> {
                Column(modifier = Modifier.padding(innerPadding)) {
                    if (!isSingleTabScreen) {
                        SecondaryTabRow(selectedTabIndex = pagerState.currentPage) {
                            tabs.forEachIndexed { index, screen ->
                                Tab(
                                    selected = pagerState.currentPage == index,
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    },
                                    text = {
                                        Text(
                                            text = stringResource(id = screen.title),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    },
                                    selectedContentColor = MaterialTheme.colorScheme.primary,
                                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.Top,
                        userScrollEnabled = !isSingleTabScreen,
                    ) { page ->
                        when (page) {
                            0 -> {
                                ViewCourseWorkContent(
                                    uiState = uiState,
                                    onEvent = onEvent,
                                    courseWork = courseWork!!,
                                    isCurrentUserTeacher = isCurrentUserTeacher,
                                    fileUtils = fileUtils,
                                    contentPadding =
                                        PaddingValues(
                                            start = 16.dp,
                                            top = 12.dp,
                                            end = 16.dp,
                                            bottom = if (isFabVisible) 100.dp else 12.dp,
                                        ),
                                    onNavigateToImageViewer = onNavigateToImageViewer,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }

                            1 -> {
                                StudentWorkScreen(
                                    snackbarHostState = snackbarHostState,
                                    courseWork = courseWork!!,
                                    isRefreshing = uiState.isRefreshing,
                                    onNavigateToViewStudentSubmission = onNavigateToViewStudentSubmission,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    CommentsBottomSheet(
        uiState = commentsUiState,
        onEvent = commentsOnEvent,
        show = uiState.showCommentsBottomSheet,
        currentUserRole = currentUserRole,
        currentUserId = uiState.currentUserId.orEmpty(),
        onDismissRequest = {
            onEvent(ViewCourseWorkUiEvent.OnShowCommentsBottomSheetChange(false))
        },
    )

    ProgressDialog(
        open = uiState.openProgressDialog,
        onDismissRequest = {},
    )
}
