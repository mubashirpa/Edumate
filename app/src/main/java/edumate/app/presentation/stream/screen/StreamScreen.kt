package edumate.app.presentation.stream.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.core.DataState
import edumate.app.core.UiText
import edumate.app.core.utils.DevicePreviews
import edumate.app.domain.model.announcements.Announcement
import edumate.app.domain.model.announcements.Material
import edumate.app.domain.model.courses.Course
import edumate.app.presentation.components.AnimatedErrorScreen
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.TextAvatar
import edumate.app.presentation.stream.StreamUiEvent
import edumate.app.presentation.stream.StreamUiState
import edumate.app.presentation.stream.screen.components.AnnouncementsListItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun StreamScreen(
    uiState: StreamUiState,
    onEvent: (StreamUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    course: Course,
    onBackPressed: () -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val context = LocalContext.current
    val refreshState = rememberPullRefreshState(
        refreshing = uiState.refreshing,
        onRefresh = { onEvent(StreamUiEvent.OnRefresh) }
    )

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(StreamUiEvent.UserMessageShown)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                if (course.section.isNullOrBlank()) {
                    Text(text = course.name)
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = course.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = course.section,
                            modifier = Modifier.padding(top = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = Strings.navigate_up)
                    )
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = null
                    )
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
                is DataState.EMPTY -> {
                    AnimatedErrorScreen(
                        url = "https://assets4.lottiefiles.com/packages/lf20_hxart9lz.json",
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = dataState.message.asString()
                    )
                }

                is DataState.ERROR -> {
                    ErrorScreen(
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = dataState.message.asString()
                    )
                }

                DataState.LOADING -> {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }

                DataState.SUCCESS -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        content = {
                            item {
                                ElevatedCard(onClick = { /*TODO*/ }) {
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = "Share with your class...",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        leadingContent = {
                                            TextAvatar(
                                                id = "8714318638",
                                                firstName = "Mubashir",
                                                lastName = "P A"
                                            )
                                        }
                                    )
                                }
                            }
                            items(uiState.announcements) { announcement ->
                                AnnouncementsListItem(announcement = announcement)
                            }
                        }
                    )
                }

                DataState.UNKNOWN -> {}
            }

            PullRefreshIndicator(
                uiState.refreshing,
                refreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@DevicePreviews
@Composable
private fun StreamScreenPreview() {
    val announcements: List<Announcement> = listOf(
        Announcement(
            text = "This is the content with attachments",
            materials = listOf(Material(), Material())
        ),
        Announcement(
            text = "This is the second content shared with class."
        ),
        Announcement(
            text = "This is the first content shared with class."
        )
    )
    val uiState = StreamUiState(
        announcements = announcements,
        dataState = DataState.EMPTY(UiText.Empty)
    )
    StreamScreen(
        uiState = uiState,
        onEvent = {},
        snackbarHostState = SnackbarHostState(),
        course = Course(
            name = "Compiler Design",
            section = "CSE"
        ),
        onBackPressed = {}
    )
}