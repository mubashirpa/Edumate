package edumate.app.presentation.stream.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.core.Constants
import edumate.app.core.DataState
import edumate.app.domain.model.courses.Course
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.components.AnimatedErrorScreen
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.components.UserAvatar
import edumate.app.presentation.stream.StreamUiEvent
import edumate.app.presentation.stream.StreamUiState
import edumate.app.presentation.stream.screen.components.AnnouncementsListItem
import edumate.app.presentation.stream.screen.components.CourseTitleBanner
import edumate.app.presentation.stream.screen.components.DeleteAnnouncementDialog

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun StreamScreen(
    uiState: StreamUiState,
    onEvent: (StreamUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    course: Course,
    navigateToCreateAnnouncement: (courseId: String) -> Unit,
    navigateToEditAnnouncement: (courseId: String, id: String) -> Unit,
    navigateToViewAnnouncement: (courseId: String, id: String) -> Unit,
    onBackPressed: () -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val context = LocalContext.current
    val refreshState = rememberPullRefreshState(
        refreshing = uiState.refreshing,
        onRefresh = { onEvent(StreamUiEvent.OnRefresh) }
    )
    val currentUserType =
        if (course.teacherGroupId.contains(uiState.currentUser?.uid)) {
            UserType.TEACHER
        } else {
            UserType.STUDENT
        }

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(StreamUiEvent.UserMessageShown)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = Strings.navigate_up)
                    )
                }
            },
            actions = {
                if (currentUserType == UserType.TEACHER) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null
                        )
                    }
                }
                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                    IconButton(
                        onClick = { onEvent(StreamUiEvent.OnAppBarMenuExpandedChange(true)) }
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                    DropdownMenu(
                        expanded = uiState.appBarMenuExpanded,
                        onDismissRequest = {
                            onEvent(StreamUiEvent.OnAppBarMenuExpandedChange(false))
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = Strings.refresh)) },
                            onClick = {
                                onEvent(StreamUiEvent.OnAppBarMenuExpandedChange(false))
                                onEvent(StreamUiEvent.OnRefresh)
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
                is DataState.ERROR -> {
                    ErrorScreen(
                        onRetryClick = { onEvent(StreamUiEvent.OnRetry) },
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
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        content = {
                            item {
                                CourseTitleBanner(course = course)
                            }
                            item {
                                OutlinedCard(
                                    onClick = { navigateToCreateAnnouncement(course.id) },
                                    border = BorderStroke(
                                        width = Dp.Hairline,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                ) {
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = stringResource(
                                                    id = Strings.share_with_your_class___
                                                ),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        leadingContent = {
                                            UserAvatar(
                                                id = uiState.currentUser?.uid.orEmpty(),
                                                fullName = uiState.currentUser?.displayName
                                                    ?: uiState.currentUser?.email.orEmpty(),
                                                photoUri = uiState.currentUser?.photoUrl
                                            )
                                        }
                                    )
                                }
                            }
                            if (dataState is DataState.EMPTY) {
                                item {
                                    AnimatedErrorScreen(
                                        url = Constants.STREAM_SCREEN_EMPTY_ANIM_URL,
                                        modifier = Modifier.fillMaxSize(),
                                        errorMessage = dataState.message.asString()
                                    )
                                }
                            } else if (dataState == DataState.SUCCESS) {
                                items(uiState.announcements, key = { it.id }) { announcement ->
                                    AnnouncementsListItem(
                                        announcement = announcement,
                                        modifier = Modifier.animateItemPlacement(),
                                        currentUserId = uiState.currentUser?.uid.orEmpty(),
                                        currentUserType = currentUserType,
                                        onClick = {
                                            navigateToViewAnnouncement(
                                                course.id,
                                                announcement.id
                                            )
                                        },
                                        onEditClick = {
                                            navigateToEditAnnouncement(
                                                course.id,
                                                announcement.id
                                            )
                                        },
                                        onDeleteClick = {
                                            onEvent(
                                                StreamUiEvent.OnOpenDeleteAnnouncementDialogChange(
                                                    announcement.id
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }

            PullRefreshIndicator(
                uiState.refreshing,
                refreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }

    DeleteAnnouncementDialog(
        onDismissRequest = { onEvent(StreamUiEvent.OnOpenDeleteAnnouncementDialogChange(null)) },
        announcementId = uiState.deleteAnnouncementId,
        onConfirmClick = { onEvent(StreamUiEvent.OnDeleteAnnouncement(it)) }
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
}