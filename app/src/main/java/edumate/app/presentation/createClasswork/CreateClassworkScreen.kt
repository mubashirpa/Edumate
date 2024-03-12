package edumate.app.presentation.createClasswork

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.classroom.courseWork.CourseWorkType
import edumate.app.presentation.components.AddAttachmentBottomSheet
import edumate.app.presentation.components.AddLinkDialog
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.createClasswork.components.ContentAssignment
import edumate.app.presentation.createClasswork.components.ContentMaterial
import edumate.app.presentation.createClasswork.components.ContentQuestion
import kotlinx.coroutines.flow.Flow
import edumate.app.R.string as Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateClassworkScreen(
    uiState: CreateClassworkUiState,
    onEvent: (CreateClassworkUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    createClassworkResults: Flow<String>,
    className: String,
    classworkId: String?,
    onCreateClassworkSuccess: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)
    val context = LocalContext.current
    val fileUtils =
        remember {
            FileUtils(context)
        }
    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                onEvent(CreateClassworkUiEvent.OnFilePicked(uri, fileUtils))
            }
        }

    LaunchedEffect(context) {
        createClassworkResults.collect {
            onCreateClassworkSuccess()
        }
    }

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(CreateClassworkUiEvent.UserMessageShown)
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .navigationBarsPadding()
                .imePadding(),
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
                        className = className,
                        classworkId = classworkId,
                        uiState = uiState,
                        onEvent = onEvent,
                    )
                }

                null -> {
                    ContentMaterial(
                        className = className,
                        classworkId = classworkId,
                        uiState = uiState,
                        onEvent = onEvent,
                    )
                }

                else -> {
                    ContentQuestion(
                        className = className,
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

class LoremIpsumSingleWord : LoremIpsum(1)
