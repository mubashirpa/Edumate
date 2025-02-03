package app.edumate.presentation.stream

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.Constants
import app.edumate.core.Result
import app.edumate.core.utils.ClipboardUtils
import app.edumate.core.utils.FileUtils
import app.edumate.domain.model.course.CourseWithMembers
import app.edumate.domain.model.material.Material
import app.edumate.domain.model.member.UserRole
import app.edumate.presentation.components.AddAttachmentBottomSheet
import app.edumate.presentation.components.AddLinkDialog
import app.edumate.presentation.components.AnimatedErrorScreen
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.components.ProgressDialog
import app.edumate.presentation.courseDetails.CourseUserRole
import app.edumate.presentation.stream.components.AnnouncementListItem
import app.edumate.presentation.stream.components.AnnouncementReplyBottomSheet
import app.edumate.presentation.stream.components.AnnouncementUserRole
import app.edumate.presentation.stream.components.DeleteAnnouncementDialog
import app.edumate.presentation.theme.EdumateTheme
import kotlinx.coroutines.launch
import java.io.File

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
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val fileUtils = remember { FileUtils(context) }
    val filePicker =
        rememberLauncherForActivityResult(GetContent()) { uri ->
            if (uri != null) {
                val title =
                    fileUtils.getFileName(uri)
                        ?: "${uri.lastPathSegment}.${fileUtils.getFileExtension(uri)}"
                val bytes = fileUtils.uriToByteArray(uri)
                val file = File(context.cacheDir, title)
                file.writeBytes(bytes)
                onEvent(StreamUiEvent.OnFilePicked(file, title))
            }
        }
    val photoPicker =
        rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
            if (uri != null) {
                val title =
                    fileUtils.getFileName(uri)
                        ?: "${uri.lastPathSegment}.${fileUtils.getFileExtension(uri)}"
                val bytes = fileUtils.uriToByteArray(uri)
                val file = File(context.cacheDir, title)
                file.writeBytes(bytes)
                onEvent(StreamUiEvent.OnFilePicked(file, title))
            }
        }

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
                modifier = Modifier.padding(bottom = 70.dp),
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
                            if (announcements.isEmpty()) {
                                AnimatedErrorScreen(
                                    url = Constants.Lottie.ANIM_STREAM_SCREEN_EMPTY,
                                    modifier = Modifier.fillMaxSize(),
                                    errorMessage = stringResource(id = R.string.start_a_conversation_with_your_class),
                                )
                            } else {
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
                                            selected = announcement.id == uiState.editAnnouncementId,
                                            onEditClick = {
                                                onEvent(
                                                    StreamUiEvent.OnEditAnnouncement(
                                                        announcement,
                                                    ),
                                                )
                                                focusRequester.requestFocus()
                                            },
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
                                            onClearSelection = {
                                                onEvent(StreamUiEvent.OnEditAnnouncement(null))
                                            },
                                            onClick = { id ->
                                                onEvent(
                                                    StreamUiEvent.OnShowReplyBottomSheetChange(
                                                        id,
                                                    ),
                                                )
                                            },
                                            modifier = Modifier.animateItem(),
                                        )
                                    }
                                }
                            }
                        }
                        AttachmentsContent(
                            attachments = uiState.attachments,
                            onEvent = onEvent,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(top = 12.dp)
                                    .padding(horizontal = 16.dp),
                        )
                        AnnouncementTextField(
                            state = uiState.text,
                            onEvent = onEvent,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 12.dp, bottom = 16.dp)
                                    .focusRequester(focusRequester),
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

    AddAttachmentBottomSheet(
        show = uiState.showAddAttachmentBottomSheet,
        onDismissRequest = {
            onEvent(StreamUiEvent.OnShowAddAttachmentBottomSheetChange(false))
        },
        onInsertLinkClick = {
            onEvent(StreamUiEvent.OnOpenAddLinkDialogChange(true))
        },
        onUploadFileClick = {
            filePicker.launch("*/*")
        },
        onPickPhotoClick = {
            photoPicker.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        },
    )

    AddLinkDialog(
        open = uiState.openAddLinkDialog,
        onDismissRequest = {
            onEvent(StreamUiEvent.OnOpenAddLinkDialogChange(false))
        },
        onConfirmClick = {
            onEvent(StreamUiEvent.OnAddLinkAttachment(it))
        },
    )

    AnnouncementReplyBottomSheet(
        commentsResult = uiState.commentsResult,
        show = uiState.replyAnnouncementId != null,
        onDismissRequest = {
            onEvent(StreamUiEvent.OnShowReplyBottomSheetChange(null))
        },
    )

    ProgressDialog(
        open = uiState.openProgressDialog,
        onDismissRequest = {},
    )

    ProgressDialog(
        open = uiState.uploadProgress != null,
        progress = { uiState.uploadProgress ?: 0.0f },
        onDismissRequest = {},
    )
}

@Composable
private fun AttachmentsContent(
    attachments: List<Material>,
    onEvent: (StreamUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (attachments.isNotEmpty()) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            attachments.forEachIndexed { index, attachment ->
                var title: String = ""
                var icon: ImageVector = Icons.Default.Attachment

                when {
                    attachment.driveFile != null -> {
                        title = attachment.driveFile.title.orEmpty()
                        icon = Icons.AutoMirrored.Filled.InsertDriveFile
                    }

                    attachment.link != null -> {
                        title = attachment.link.title.orEmpty()
                        icon = Icons.Default.Link
                    }
                }

                AssistChip(
                    onClick = {
                        onEvent(StreamUiEvent.OnRemoveAttachment(index))
                    },
                    label = {
                        Text(
                            text = title,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    },
                    modifier = Modifier.widthIn(max = 180.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun AnnouncementTextField(
    state: TextFieldState,
    onEvent: (StreamUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        state = state,
        modifier = modifier,
        placeholder = {
            Text(text = stringResource(R.string.announce_something_your_class))
        },
        leadingIcon = {
            IconButton(
                onClick = {
                    onEvent(StreamUiEvent.OnShowAddAttachmentBottomSheetChange(true))
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
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
        keyboardOptions =
            KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrectEnabled = true,
                imeAction = ImeAction.Send,
            ),
        onKeyboardAction = {
            onEvent(StreamUiEvent.CreateAnnouncement)
        },
        lineLimits = TextFieldLineLimits.SingleLine,
        shape = CircleShape,
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
