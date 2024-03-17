package edumate.app.presentation.createClasswork

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.classroom.courseWork.CourseWorkType
import edumate.app.presentation.components.AddAttachmentBottomSheet
import edumate.app.presentation.components.AddLinkDialog
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.createClasswork.components.ContentAssignment
import edumate.app.presentation.createClasswork.components.ContentMaterial
import edumate.app.presentation.createClasswork.components.ContentQuestion
import edumate.app.presentation.ui.theme.EdumateTheme
import kotlinx.coroutines.flow.filter
import edumate.app.R.string as Strings

@Composable
fun CreateClassworkScreen(
    viewModel: CreateClassworkViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    courseName: String,
    classworkId: String?,
    onCreateClassworkSuccess: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnCreateClassworkSuccess by rememberUpdatedState(onCreateClassworkSuccess)
    val context = LocalContext.current

    LaunchedEffect(viewModel, lifecycle) {
        // Whenever the uiState changes, check if the classwork is created and call the
        // `onCreateAnnouncementSuccess` event when `lifecycle` is at least STARTED
        snapshotFlow { viewModel.uiState }
            .filter { it.isCreateClassworkSuccess }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnCreateClassworkSuccess()
            }
    }

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(CreateClassworkUiEvent.UserMessageShown)
        }
    }

    CreateClassworkScreenContent(
        uiState = viewModel.uiState,
        onEvent = viewModel::onEvent,
        courseName = courseName,
        classworkId = classworkId,
        onBackPressed = onBackPressed,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateClassworkScreenContent(
    uiState: CreateClassworkUiState,
    onEvent: (CreateClassworkUiEvent) -> Unit,
    courseName: String,
    classworkId: String?,
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
                onEvent(CreateClassworkUiEvent.OnFilePicked(uri, title))
            }
        }

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
            when (uiState.workType) {
                CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED -> {
                    // Nothing is shown
                }

                CourseWorkType.ASSIGNMENT -> {
                    ContentAssignment(
                        courseName = courseName,
                        classworkId = classworkId,
                        uiState = uiState,
                        onEvent = onEvent,
                    )
                }

                null -> {
                    ContentMaterial(
                        courseName = courseName,
                        classworkId = classworkId,
                        uiState = uiState,
                        onEvent = onEvent,
                    )
                }

                else -> {
                    ContentQuestion(
                        courseName = courseName,
                        classworkId = classworkId,
                        uiState = uiState,
                        onEvent = onEvent,
                    )
                }
            }
        }
    }

    AddAttachmentBottomSheet(
        onDismissRequest = {
            onEvent(CreateClassworkUiEvent.OnShowAddAttachmentBottomSheetChange(false))
        },
        showBottomSheet = uiState.showAddAttachmentBottomSheet,
        onInsertLinkClick = {
            onEvent(CreateClassworkUiEvent.OnOpenAddLinkDialogChange(true))
        },
        onUploadFileClick = {
            filePicker.launch("*/*")
        },
    )

    AddLinkDialog(
        onDismissRequest = {
            onEvent(CreateClassworkUiEvent.OnOpenAddLinkDialogChange(false))
        },
        openDialog = uiState.openAddLinkDialog,
        onConfirmClick = {
            onEvent(CreateClassworkUiEvent.OnAddLinkAttachment(it))
        },
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
}

@Preview(showBackground = true)
@Composable
private fun CreateClassworkScreenPreview(
    @PreviewParameter(CourseName::class) courseName: String,
) {
    EdumateTheme {
        CreateClassworkScreenContent(
            uiState = CreateClassworkUiState(),
            onEvent = {},
            courseName = courseName,
            classworkId = null,
            onBackPressed = {},
        )
    }
}

internal class CourseName : LoremIpsum(2)
