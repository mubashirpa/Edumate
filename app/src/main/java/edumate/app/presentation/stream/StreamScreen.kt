package edumate.app.presentation.stream

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edumate.app.core.Constants
import edumate.app.core.Result
import edumate.app.core.utils.ClipboardUtils
import edumate.app.domain.model.classroom.courses.Course
import edumate.app.presentation.components.AnimatedErrorScreen
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.components.UserAvatar
import edumate.app.presentation.stream.components.AnnouncementListItem
import edumate.app.presentation.stream.components.DeleteAnnouncementDialog
import edumate.app.presentation.ui.theme.EdumateTheme
import kotlinx.coroutines.launch
import edumate.app.R.string as Strings

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class,
)
@Composable
fun StreamScreen(
    uiState: StreamUiState,
    onEvent: (StreamUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    course: Course,
    navigateToCreateAnnouncement: (courseId: String) -> Unit,
    navigateToEditAnnouncement: (courseId: String, id: String?) -> Unit,
    navigateToViewAnnouncement: (courseId: String, id: String) -> Unit,
    onBackPressed: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val isTeacher = course.teachers?.any { it.userId == uiState.user?.id } == true
    val courseId = course.id.orEmpty()
    val courseName = course.name.orEmpty()
    val refreshState =
        rememberPullRefreshState(
            refreshing = uiState.isRefreshing,
            onRefresh = {
                onEvent(StreamUiEvent.OnRefresh)
            },
        )
    val showTopAppBarTitle by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex != 0
        }
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
            title = {
                AnimatedVisibility(visible = showTopAppBarTitle) {
                    Text(
                        text = courseName,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
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
                if (isTeacher) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(context.getString(Strings.not_available))
                            }
                        },
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                        )
                    }
                }
                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                    IconButton(
                        onClick = { onEvent(StreamUiEvent.OnAppBarDropdownExpandedChange(true)) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                        )
                    }
                    DropdownMenu(
                        expanded = uiState.appBarDropdownExpanded,
                        onDismissRequest = {
                            onEvent(StreamUiEvent.OnAppBarDropdownExpandedChange(false))
                        },
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(id = Strings.refresh))
                            },
                            onClick = {
                                onEvent(StreamUiEvent.OnAppBarDropdownExpandedChange(false))
                                onEvent(StreamUiEvent.OnRefresh)
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
            when (val announcementsResult = uiState.announcementsResult) {
                is Result.Empty -> {
                    // Nothing is shown
                }

                is Result.Error -> {
                    ErrorScreen(
                        onRetryClick = {
                            onEvent(StreamUiEvent.OnRetry)
                        },
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                        errorMessage = announcementsResult.message!!.asString(),
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
                    val announcements = announcementsResult.data

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = scrollState,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        content = {
                            item {
                                Card(modifier = Modifier.aspectRatio(8f / 3f)) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        AsyncImage(
                                            model =
                                                ImageRequest.Builder(LocalContext.current)
                                                    .data(course.photoUrl)
                                                    .crossfade(true)
                                                    .build(),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.FillBounds,
                                        )
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.Bottom,
                                        ) {
                                            ListItem(
                                                headlineContent = {
                                                    Text(
                                                        text = courseName,
                                                        color = Color.White,
                                                        overflow = TextOverflow.Ellipsis,
                                                        maxLines = 1,
                                                        style = MaterialTheme.typography.headlineSmall,
                                                    )
                                                },
                                                supportingContent = {
                                                    course.section?.let {
                                                        Text(
                                                            text = it,
                                                            color = Color.White,
                                                            overflow = TextOverflow.Ellipsis,
                                                            maxLines = 1,
                                                            style = MaterialTheme.typography.bodyLarge,
                                                        )
                                                    }
                                                },
                                                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                OutlinedCard(
                                    onClick = {
                                        navigateToCreateAnnouncement(courseId)
                                    },
                                    border =
                                        BorderStroke(
                                            width = Dp.Hairline,
                                            color = MaterialTheme.colorScheme.outline,
                                        ),
                                ) {
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = stringResource(id = Strings.announce_something_to_your_class),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                        },
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        leadingContent = {
                                            UserAvatar(
                                                id = uiState.user?.id.orEmpty(),
                                                fullName = uiState.user?.name?.fullName.orEmpty(),
                                                photoUrl = uiState.user?.photoUrl,
                                            )
                                        },
                                    )
                                }
                            }
                            if (announcements.isNullOrEmpty()) {
                                item {
                                    AnimatedErrorScreen(
                                        url = Constants.ANIM_STREAM_SCREEN_EMPTY,
                                        modifier = Modifier.fillMaxWidth(),
                                        errorMessage = stringResource(id = Strings.start_a_conversation_with_your_class),
                                    )
                                }
                            } else {
                                items(
                                    items = announcements,
                                    key = { it.id!! },
                                ) { announcement ->
                                    AnnouncementListItem(
                                        announcement = announcement,
                                        modifier = Modifier.animateItemPlacement(),
                                        course = course,
                                        userId = uiState.user?.id.orEmpty(),
                                        onClick = { id ->
                                            navigateToViewAnnouncement(courseId, id)
                                        },
                                        onEditClick = { id ->
                                            navigateToEditAnnouncement(courseId, id)
                                        },
                                        onDeleteClick = { id ->
                                            onEvent(
                                                StreamUiEvent.OnOpenDeleteAnnouncementDialogChange(
                                                    id,
                                                ),
                                            )
                                        },
                                        onCopyLinkClick = { link ->
                                            ClipboardUtils.copyTextToClipboard(context, link) {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        context.getString(
                                                            Strings.link_copied,
                                                        ),
                                                    )
                                                }
                                            }
                                        },
                                    )
                                }
                            }
                        },
                    )
                }
            }

            PullRefreshIndicator(
                refreshing = uiState.isRefreshing,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }

    DeleteAnnouncementDialog(
        onDismissRequest = {
            onEvent(StreamUiEvent.OnOpenDeleteAnnouncementDialogChange(null))
        },
        open = uiState.deleteAnnouncementId != null,
        onConfirmButtonClick = {
            onEvent(StreamUiEvent.OnDeleteAnnouncement(uiState.deleteAnnouncementId!!))
        },
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
}

@Preview(showBackground = true)
@Composable
private fun StreamScreenPreview(
    @PreviewParameter(CourseDetails::class) courseDetails: String,
) {
    EdumateTheme {
        StreamScreen(
            uiState = StreamUiState(announcementsResult = Result.Success(data = null)),
            onEvent = {},
            snackbarHostState = SnackbarHostState(),
            course =
                Course(
                    name = courseDetails,
                    section = courseDetails,
                ),
            navigateToCreateAnnouncement = {},
            navigateToEditAnnouncement = { _, _ -> },
            navigateToViewAnnouncement = { _, _ -> },
            onBackPressed = {},
        )
    }
}

private class CourseDetails : LoremIpsum(2)
