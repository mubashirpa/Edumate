package edumate.app.presentation.create_classwork.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.presentation.class_details.screen.components.ClassDetailsAppBar
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
    val fileUtils = remember {
        FileUtils(context)
    }
    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                onEvent(CreateClassworkUiEvent.OnFilePicked(it, fileUtils))
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
        ClassDetailsAppBar(
            title = "",
            scrollBehavior = scrollBehavior,
            onNavigationClick = onBackPressed
        )
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

    AttachmentMenuBottomSheet(
        openBottomSheet = uiState.openAttachmentMenu,
        onInsertLinkClick = {
            onEvent(CreateClassworkUiEvent.OnOpenAddLinkDialogChange(true))
        },
        onUploadFileClick = { filePicker.launch("*/*") },
        onDismissRequest = {
            onEvent(CreateClassworkUiEvent.OnOpenAttachmentMenuChange(false))
        }
    )

    AddLinkDialog(
        openDialog = uiState.openAddLinkDialog,
        onConfirm = { onEvent(CreateClassworkUiEvent.OnAddLinkAttachment(it)) },
        onDismissRequest = {
            onEvent(CreateClassworkUiEvent.OnOpenAddLinkDialogChange(false))
        }
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
}