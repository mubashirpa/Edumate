package edumate.app.presentation.meet.screen

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.VideoCall
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.core.Constants
import edumate.app.core.DataState
import edumate.app.domain.model.courses.Course
import edumate.app.presentation.activities.MeetActivity
import edumate.app.presentation.components.AnimatedErrorScreen
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.meet.MeetUiEvent
import edumate.app.presentation.meet.MeetUiState
import edumate.app.presentation.meet.screen.components.MeetListItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MeetScreen(
    uiState: MeetUiState,
    onEvent: (MeetUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    course: Course,
    onBackPressed: () -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val context = LocalContext.current
    val refreshState = rememberPullRefreshState(
        refreshing = uiState.refreshing,
        onRefresh = { onEvent(MeetUiEvent.OnRefresh) }
    )

    LaunchedEffect(Unit) {
        onEvent(MeetUiEvent.OnCreate(course))
    }

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(MeetUiEvent.OnUserMessageShown)
        }
    }

    uiState.launchMeeting?.let { meeting ->
        LaunchedEffect(meeting) {
            val intent =
                Intent(context, MeetActivity::class.java).apply {
                    putExtra("meetingId", meeting.meetingId)
                    putExtra("title", meeting.title)
                }
            context.startActivity(intent)
            onEvent(MeetUiEvent.OnLaunchMeetingSuccess)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text(text = stringResource(id = Strings.title_meet_screen)) },
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
                        onClick = { onEvent(MeetUiEvent.OnAppBarMenuExpandedChange(true)) }
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                    DropdownMenu(
                        expanded = uiState.appBarMenuExpanded,
                        onDismissRequest = {
                            onEvent(MeetUiEvent.OnAppBarMenuExpandedChange(false))
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = Strings.refresh)) },
                            onClick = {
                                onEvent(MeetUiEvent.OnAppBarMenuExpandedChange(false))
                                onEvent(MeetUiEvent.OnRefresh)
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
                        onRetryClick = { onEvent(MeetUiEvent.OnRetry) },
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
                        content = {
                            item {
                                Text(
                                    text = stringResource(id = Strings.meetings),
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 10.dp
                                    ),
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            if (dataState is DataState.EMPTY) {
                                item {
                                    AnimatedErrorScreen(
                                        url = Constants.MEET_SCREEN_EMPTY_ANIM_URL,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp, vertical = 10.dp),
                                        errorMessage = if (uiState.isCurrentUserTeacher) {
                                            stringResource(
                                                id = Strings.go_live_for_your_students_and_teach_them_anytime_anywhere
                                            )
                                        } else {
                                            stringResource(
                                                id = Strings.no_ongoing_live_classes_right_now
                                            )
                                        }
                                    )
                                }
                            } else if (dataState == DataState.SUCCESS) {
                                items(uiState.meetings, key = { it.id }) { meeting ->
                                    MeetListItem(
                                        meeting = meeting,
                                        uiState = uiState,
                                        onEndClick = { onEvent(MeetUiEvent.EndMeeting(it)) },
                                        onDeleteClick = { onEvent(MeetUiEvent.DeleteMeeting(it)) },
                                        onClick = { onEvent(MeetUiEvent.StartMeeting(meeting)) }
                                    )
                                }
                            }
                        }
                    )
                }
            }

            if (uiState.isCurrentUserTeacher) {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(id = Strings.new_meeting)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.VideoCall,
                            contentDescription = "New meeting"
                        )
                    },
                    onClick = { onEvent(MeetUiEvent.CreateMeeting) },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd),
                    expanded = true
                )
            }

            PullRefreshIndicator(
                uiState.refreshing,
                refreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }

    ProgressDialog(openDialog = uiState.openProgressDialog)
}