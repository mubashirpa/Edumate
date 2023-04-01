package edumate.app.presentation.create_classwork.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.hilt.navigation.compose.hiltViewModel
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.presentation.class_details.screen.components.ClassDetailsAppBar
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.create_classwork.CreateClassworkUiEvent
import edumate.app.presentation.create_classwork.CreateClassworkViewModel
import edumate.app.presentation.create_classwork.screen.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateClassworkScreen(
    viewModel: CreateClassworkViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    className: String,
    onCreateClassworkSuccess: () -> Unit,
    onBackPressed: () -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val fileUtils = remember {
        FileUtils(context)
    }
    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.onEvent(CreateClassworkUiEvent.OnGetContent(it, fileUtils))
            }
        }

    LaunchedEffect(context) {
        viewModel.createClassworkResults.collect {
            onCreateClassworkSuccess()
        }
    }

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(CreateClassworkUiEvent.UserMessageShown)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
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
                    onTitleChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnTitleChange(it))
                    },
                    onDescriptionChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnDescriptionChange(it))
                    },
                    onOpenAttachmentMenuChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnOpenAttachmentMenuChange(it))
                    },
                    onRemoveAttachment = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnRemoveAttachment(it))
                    },
                    onPostMaterial = {
                        viewModel.onEvent(CreateClassworkUiEvent.CreateClasswork)
                    }
                )
            }
            CourseWorkType.ASSIGNMENT -> {
                ContentAssignment(
                    courseTitle = className,
                    uiState = uiState,
                    onTitleChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnTitleChange(it))
                    },
                    onDescriptionChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnDescriptionChange(it))
                    },
                    onDueDateChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnDueDateChange(it))
                    },
                    onOpenAttachmentMenuChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnOpenAttachmentMenuChange(it))
                    },
                    onOpenDatePickerDialogChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnOpenDatePickerDialogChange(it))
                    },
                    onOpenPointsDialogChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnOpenPointsDialogChange(it))
                    },
                    onOpenTimePickerDialogChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnOpenTimePickerDialogChange(it))
                    },
                    onPointsChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnPointsChange(it))
                    },
                    onRemoveAttachment = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnRemoveAttachment(it))
                    },
                    onAssignAssignment = {
                        viewModel.onEvent(CreateClassworkUiEvent.CreateClasswork)
                    }
                )
            }
            else -> {
                ContentQuestion(
                    courseTitle = className,
                    uiState = uiState,
                    onTitleChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnTitleChange(it))
                    },
                    onDescriptionChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnDescriptionChange(it))
                    },
                    onDueDateChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnDueDateChange(it))
                    },
                    onOpenAttachmentMenuChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnOpenAttachmentMenuChange(it))
                    },
                    onOpenDatePickerDialogChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnOpenDatePickerDialogChange(it))
                    },
                    onOpenPointsDialogChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnOpenPointsDialogChange(it))
                    },
                    onOpenTimePickerDialogChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnOpenTimePickerDialogChange(it))
                    },
                    onPointsChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnPointsChange(it))
                    },
                    onRemoveAttachment = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnRemoveAttachment(it))
                    },
                    onWorkTypeChange = {
                        viewModel.onEvent(CreateClassworkUiEvent.OnWorkTypeChange(it))
                    },
                    onAskQuestion = {
                        viewModel.onEvent(CreateClassworkUiEvent.CreateClasswork)
                    }
                )
            }
        }
    }

    AttachmentMenuBottomSheet(
        openBottomSheet = uiState.openAttachmentMenu,
        onInsertLinkClick = {
            viewModel.onEvent(CreateClassworkUiEvent.OnOpenAddLinkDialogChange(true))
        },
        onUploadFileClick = { filePicker.launch("*/*") },
        onDismissRequest = {
            viewModel.onEvent(CreateClassworkUiEvent.OnOpenAttachmentMenuChange(false))
        }
    )

    AddLinkDialog(
        openDialog = uiState.openAddLinkDialog,
        onConfirm = { viewModel.onEvent(CreateClassworkUiEvent.OnAddLinkAttachment(it)) },
        onDismissRequest = {
            viewModel.onEvent(CreateClassworkUiEvent.OnOpenAddLinkDialogChange(false))
        }
    )

    ProgressDialog(openDialog = viewModel.uiState.openProgressDialog)
}