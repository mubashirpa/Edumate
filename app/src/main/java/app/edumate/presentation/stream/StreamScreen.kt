package app.edumate.presentation.stream

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.utils.ClipboardUtils
import app.edumate.domain.model.course.CourseWithMembers
import app.edumate.domain.model.member.UserRole
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.components.ProgressDialog
import app.edumate.presentation.courseDetails.CourseUserRole
import app.edumate.presentation.stream.components.AnnouncementListItem
import app.edumate.presentation.stream.components.AnnouncementUserRole
import app.edumate.presentation.stream.components.DeleteAnnouncementDialog
import app.edumate.presentation.theme.EdumateTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamScreen(
    uiState: StreamUiState,
    onEvent: (StreamUiEvent) -> Unit,
    courseWithMembers: CourseWithMembers,
    currentUserRole: CourseUserRole,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(StreamUiEvent.UserMessageShown)
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
                    if (currentUserRole is CourseUserRole.Teacher) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                            )
                        }
                    }
                    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                        IconButton(
                            onClick = {
                                onEvent(StreamUiEvent.OnExpandedAppBarDropdownChange(true))
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
                                onEvent(StreamUiEvent.OnExpandedAppBarDropdownChange(false))
                            },
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(id = R.string.refresh))
                                },
                                onClick = {
                                    onEvent(StreamUiEvent.OnExpandedAppBarDropdownChange(false))
                                    onEvent(StreamUiEvent.OnRefresh)
                                },
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 68.dp),
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = {
                onEvent(StreamUiEvent.OnRefresh)
            },
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            when (val announcementResult = uiState.announcementResult) {
                is Result.Empty -> {}

                is Result.Error -> {
                    ErrorScreen(
                        onRetryClick = {
                            onEvent(StreamUiEvent.OnRetry)
                        },
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = announcementResult.message!!.asString(),
                    )
                }

                is Result.Loading -> {
                    LoadingScreen()
                }

                is Result.Success -> {
                    val announcements = announcementResult.data.orEmpty()

                    Column {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding =
                                    PaddingValues(
                                        horizontal = 16.dp,
                                        vertical = 12.dp,
                                    ),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                items(
                                    items = announcements,
                                    key = { it.id!! },
                                ) { announcement ->
                                    val isCurrentUserCreator =
                                        announcement.creatorUserId == uiState.currentUserId
                                    val announcementUserRole =
                                        courseWithMembers.members
                                            ?.find {
                                                it.userId == announcement.creatorUserId
                                            }?.role
                                    val announcementItemUserRole =
                                        when {
                                            announcementUserRole == UserRole.TEACHER -> {
                                                AnnouncementUserRole.TEACHER
                                            }

                                            else -> {
                                                AnnouncementUserRole.STUDENT
                                            }
                                        }

                                    AnnouncementListItem(
                                        announcement = announcement,
                                        currentUserRole = currentUserRole,
                                        itemUserRole = announcementItemUserRole,
                                        isCurrentUserCreator = isCurrentUserCreator,
                                        onEditClick = { /*TODO*/ },
                                        onDeleteClick = { id ->
                                            onEvent(
                                                StreamUiEvent.OnOpenDeleteAnnouncementDialogChange(
                                                    id,
                                                ),
                                            )
                                        },
                                        onCopyLinkClick = {
                                            ClipboardUtils.copyTextToClipboard(
                                                context = context,
                                                textCopied = courseWithMembers.joinLink,
                                            ) {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        context.getString(
                                                            R.string.invite_link_copied,
                                                        ),
                                                    )
                                                }
                                            }
                                        },
                                        onClick = { /*TODO*/ },
                                        modifier = Modifier.animateItem(),
                                    )
                                }
                            }
                        }
                        OutlinedTextField(
                            state = uiState.text,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                            placeholder = {
                                Text(text = stringResource(R.string.announce_something_your_class))
                            },
                            leadingIcon = {
                                BadgedBox(
                                    badge = {
                                        if (uiState.attachments.isNotEmpty()) {
                                            Badge {
                                                val badgeNumber =
                                                    uiState.attachments.size.toString()
                                                Text(
                                                    text = badgeNumber,
                                                    modifier =
                                                        Modifier.semantics {
                                                            contentDescription =
                                                                context.getString(
                                                                    R.string._attachments,
                                                                    badgeNumber,
                                                                )
                                                        },
                                                )
                                            }
                                        }
                                    },
                                ) {
                                    IconButton(onClick = { /*TODO*/ }) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                        )
                                    }
                                }
                            },
                            trailingIcon = {
                                IconButton(onClick = { onEvent(StreamUiEvent.CreateAnnouncement) }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = null,
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            onKeyboardAction = {
                                onEvent(StreamUiEvent.CreateAnnouncement)
                            },
                            lineLimits = TextFieldLineLimits.SingleLine,
                            shape = CircleShape,
                        )
                    }
                }
            }
        }
    }

    DeleteAnnouncementDialog(
        open = uiState.deleteAnnouncementId != null,
        onDismissRequest = {
            onEvent(StreamUiEvent.OnOpenDeleteAnnouncementDialogChange(null))
        },
        onConfirmButtonClick = {
            onEvent(StreamUiEvent.OnDeleteAnnouncement(uiState.deleteAnnouncementId!!))
        },
    )

    ProgressDialog(
        open = uiState.openProgressDialog,
        onDismissRequest = {},
    )
}

@Preview
@Composable
private fun StreamScreenPreview() {
    EdumateTheme {
        StreamScreen(
            uiState = StreamUiState(announcementResult = Result.Success(emptyList())),
            onEvent = {},
            courseWithMembers = CourseWithMembers(),
            currentUserRole = CourseUserRole.Teacher(true),
            onNavigateUp = {},
        )
    }
}
