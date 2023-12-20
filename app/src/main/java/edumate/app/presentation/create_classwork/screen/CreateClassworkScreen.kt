package edumate.app.presentation.create_classwork.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import edumate.app.R.string as Strings
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.presentation.components.AddLinkDialog
import edumate.app.presentation.components.AttachmentMenuBottomSheet
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.create_classwork.CreateClassworkUiEvent
import edumate.app.presentation.create_classwork.CreateClassworkUiState
import edumate.app.presentation.create_classwork.screen.components.*
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateClassworkScreen(
    uiState: CreateClassworkUiState,
    onEvent: (CreateClassworkUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    createClassworkResults: Flow<String>,
    className: String,
    onCreateClassworkSuccess: () -> Unit,
    onBackPressed: () -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)
    val context = LocalContext.current
    val fileUtils = remember { FileUtils(context) }
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
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .navigationBarsPadding()
            .imePadding()
    ) {
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
            scrollBehavior = scrollBehavior
        )
        if (uiState.loading) {
            LoadingIndicator(modifier = Modifier.fillMaxSize())
        } else {
            when (uiState.workType) {
                CourseWorkType.MATERIAL -> {
                    ContentMaterial(
                        courseTitle = className,
                        uiState = uiState,
                        onEvent = onEvent
                    )
                }

                CourseWorkType.ASSIGNMENT -> {
                    ContentAssignment(
                        courseTitle = className,
                        uiState = uiState,
                        onEvent = onEvent
                    )
                }

                else -> {
                    ContentQuestion(
                        courseTitle = className,
                        uiState = uiState,
                        onEvent = onEvent
                    )
                }
            }
        }
    }

    AttachmentMenuBottomSheet(
        onDismissRequest = { onEvent(CreateClassworkUiEvent.OnOpenAttachmentMenuChange(false)) },
        openBottomSheet = uiState.openAttachmentMenu,
        onInsertLinkClick = { onEvent(CreateClassworkUiEvent.OnOpenAddLinkDialogChange(true)) },
        onUploadFileClick = { filePicker.launch("*/*") }
    )

    AddLinkDialog(
        onDismissRequest = { onEvent(CreateClassworkUiEvent.OnOpenAddLinkDialogChange(false)) },
        openDialog = uiState.openAddLinkDialog,
        onConfirmClick = { onEvent(CreateClassworkUiEvent.OnAddLinkAttachment(it)) }
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
}