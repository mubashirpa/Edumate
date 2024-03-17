package edumate.app.presentation.createAnnouncement

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edumate.app.core.utils.FileType
import edumate.app.core.utils.FileUtils
import edumate.app.presentation.components.AddAttachmentBottomSheet
import edumate.app.presentation.components.AddLinkDialog
import edumate.app.presentation.components.FieldListItem
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.ui.theme.EdumateTheme
import kotlinx.coroutines.flow.filter
import edumate.app.R.string as Strings

@Composable
fun CreateAnnouncementScreen(
    viewModel: CreateAnnouncementViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    courseName: String,
    announcementId: String?,
    onCreateAnnouncementSuccess: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnCreateAnnouncementSuccess by rememberUpdatedState(onCreateAnnouncementSuccess)
    val context = LocalContext.current

    LaunchedEffect(viewModel, lifecycle) {
        // Whenever the uiState changes, check if the announcement is created and call the
        // `onCreateAnnouncementSuccess` event when `lifecycle` is at least STARTED
        snapshotFlow { viewModel.uiState }
            .filter { it.isCreateAnnouncementSuccess }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnCreateAnnouncementSuccess()
            }
    }

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(CreateAnnouncementUiEvent.UserMessageShown)
        }
    }

    CreateAnnouncementScreenContent(
        uiState = viewModel.uiState,
        onEvent = viewModel::onEvent,
        courseName = courseName,
        announcementId = announcementId,
        onBackPressed = onBackPressed,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateAnnouncementScreenContent(
    uiState: CreateAnnouncementUiState,
    onEvent: (CreateAnnouncementUiEvent) -> Unit,
    courseName: String,
    announcementId: String?,
    onBackPressed: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val fileUtils =
        remember {
            FileUtils(context)
        }
    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val title =
                    fileUtils.getFileName(uri)
                        ?: "${uri.lastPathSegment}.${fileUtils.getFileExtension(uri)}"
                onEvent(CreateAnnouncementUiEvent.OnFilePicked(uri, title))
            }
        }
    val focusRequester =
        remember {
            FocusRequester()
        }
    val isTextError = uiState.textError != null

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .navigationBarsPadding(),
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = Strings.navigate_up),
                    )
                }
            },
            scrollBehavior = scrollBehavior,
        )
        if (uiState.isLoading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
            ) {
                FieldListItem(
                    headlineContent = {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline,
                                        shape = MaterialTheme.shapes.extraSmall,
                                    )
                                    .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Spacer(modifier = Modifier.width(16.dp))
                            ElevatedSuggestionChip(
                                onClick = {},
                                label = {
                                    Text(text = courseName)
                                },
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            ElevatedSuggestionChip(
                                onClick = {},
                                label = {
                                    Text(text = stringResource(id = Strings.all_students))
                                },
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    },
                    leadingIcon = Icons.Outlined.People,
                    trailingContent = {},
                )
                FieldListItem(
                    headlineContent = {
                        val textError = uiState.textError
                        OutlinedTextField(
                            value = uiState.text,
                            onValueChange = {
                                onEvent(CreateAnnouncementUiEvent.OnTextValueChange(it))
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                            label = {
                                Text(text = stringResource(id = Strings.announce_something_with_your_class))
                            },
                            supportingText =
                                if (isTextError) {
                                    {
                                        Text(text = textError!!.asString())
                                    }
                                } else {
                                    null
                                },
                            isError = isTextError,
                            keyboardOptions =
                                KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    autoCorrect = true,
                                ),
                        )
                    },
                    leadingIcon = Icons.AutoMirrored.Filled.Subject,
                    trailingContent = {},
                )
                FieldListItem(
                    headlineContent = {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 56.dp)
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline,
                                        shape = MaterialTheme.shapes.extraSmall,
                                    ),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            uiState.attachments.onEachIndexed { index, material ->
                                ListItem(
                                    headlineContent = {
                                        val title: String =
                                            if (material.driveFile != null) {
                                                material.driveFile.title.orEmpty()
                                            } else {
                                                material.link?.title.orEmpty()
                                            }
                                        Text(
                                            text = title,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1,
                                        )
                                    },
                                    leadingContent = {
                                        if (material.link?.thumbnailUrl.isNullOrEmpty()) {
                                            val icon =
                                                if (material.driveFile != null) {
                                                    val mimeType =
                                                        fileUtils.getMimeType(Uri.parse(material.driveFile.alternateLink.orEmpty()))
                                                    when (fileUtils.getFileType(mimeType)) {
                                                        FileType.IMAGE -> Icons.Default.Image
                                                        FileType.VIDEO -> Icons.Default.VideoFile
                                                        FileType.AUDIO -> Icons.Default.AudioFile
                                                        FileType.PDF -> Icons.Default.PictureAsPdf
                                                        FileType.UNKNOWN -> Icons.AutoMirrored.Filled.InsertDriveFile
                                                    }
                                                } else if (material.link != null) {
                                                    Icons.Default.Link
                                                } else {
                                                    Icons.Default.Attachment
                                                }
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                            )
                                        } else {
                                            AsyncImage(
                                                model =
                                                    ImageRequest.Builder(LocalContext.current)
                                                        .data(material.link?.thumbnailUrl)
                                                        .crossfade(true)
                                                        .build(),
                                                contentDescription = null,
                                                contentScale = ContentScale.Inside,
                                                modifier = Modifier.size(24.dp),
                                            )
                                        }
                                    },
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                onEvent(
                                                    CreateAnnouncementUiEvent.OnRemoveAttachment(
                                                        index,
                                                    ),
                                                )
                                            },
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = null,
                                            )
                                        }
                                    },
                                )
                                HorizontalDivider()
                            }
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = stringResource(id = Strings.add_attachment),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                },
                                modifier =
                                    Modifier.clickable {
                                        onEvent(
                                            CreateAnnouncementUiEvent.OnShowAddAttachmentBottomSheetChange(
                                                true,
                                            ),
                                        )
                                    },
                            )
                        }
                    },
                    leadingIcon = Icons.Default.Attachment,
                    trailingContent = {},
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    onEvent(CreateAnnouncementUiEvent.PostAnnouncement)
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
            ) {
                Text(
                    text =
                        if (announcementId != null) {
                            stringResource(id = Strings.save)
                        } else {
                            stringResource(id = Strings.post)
                        },
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            LaunchedEffect(true) {
                focusRequester.requestFocus()
            }
        }
    }

    AddAttachmentBottomSheet(
        onDismissRequest = {
            onEvent(CreateAnnouncementUiEvent.OnShowAddAttachmentBottomSheetChange(false))
        },
        showBottomSheet = uiState.showAddAttachmentBottomSheet,
        onInsertLinkClick = {
            onEvent(CreateAnnouncementUiEvent.OnOpenAddLinkDialogChange(true))
        },
        onUploadFileClick = {
            filePicker.launch("*/*")
        },
    )

    AddLinkDialog(
        onDismissRequest = {
            onEvent(CreateAnnouncementUiEvent.OnOpenAddLinkDialogChange(false))
        },
        openDialog = uiState.openAddLinkDialog,
        onConfirmClick = {
            onEvent(CreateAnnouncementUiEvent.OnAddLinkAttachment(it))
        },
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
}

@Preview(showBackground = true)
@Composable
private fun CreateAnnouncementScreenPreview(
    @PreviewParameter(CourseName::class) courseName: String,
) {
    EdumateTheme {
        CreateAnnouncementScreenContent(
            uiState = CreateAnnouncementUiState(),
            onEvent = {},
            courseName = courseName,
            announcementId = null,
            onBackPressed = {},
        )
    }
}

private class CourseName : LoremIpsum(2)
