package app.edumate.presentation.createCourseWork

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import app.edumate.core.utils.FileUtils
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.presentation.components.AddAttachmentBottomSheet
import app.edumate.presentation.components.AddLinkDialog
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.components.ProgressDialog
import app.edumate.presentation.createCourseWork.components.ContentAssignment
import app.edumate.presentation.createCourseWork.components.ContentMaterial
import app.edumate.presentation.createCourseWork.components.ContentQuestion
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun CreateCourseWorkScreen(
    courseName: String,
    onNavigateUp: () -> Unit,
    onCreateCourseWorkComplete: () -> Unit,
    modifier: Modifier = Modifier,
    courseWorkId: String? = null,
    viewModel: CreateCourseWorkViewModel = koinViewModel(),
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnCreateCourseWorkComplete by rememberUpdatedState(onCreateCourseWorkComplete)

    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { viewModel.uiState }
            .filter { it.isCreateCourseWorkSuccess }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnCreateCourseWorkComplete()
            }
    }

    CreateCourseWorkContent(
        uiState = viewModel.uiState,
        onEvent = viewModel::onEvent,
        courseName = courseName,
        onNavigateUp = onNavigateUp,
        modifier = modifier,
        courseWorkId = courseWorkId,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateCourseWorkContent(
    uiState: CreateCourseWorkUiState,
    onEvent: (CreateCourseWorkUiEvent) -> Unit,
    courseName: String,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    courseWorkId: String? = null,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val fileUtils = remember { FileUtils(context) }
    val filePicker =
        rememberLauncherForActivityResult(GetContent()) {
            it?.let { uri ->
                onEvent(uri.handleFile(fileUtils, context))
            }
        }
    val photoPicker =
        rememberLauncherForActivityResult(PickVisualMedia()) {
            it?.let { uri ->
                onEvent(uri.handleFile(fileUtils, context))
            }
        }

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(CreateCourseWorkUiEvent.UserMessageShown)
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        if (uiState.isLoading) {
            LoadingScreen(modifier = contentModifier)
        } else {
            when (uiState.workType) {
                CourseWorkType.ASSIGNMENT -> {
                    ContentAssignment(
                        uiState = uiState,
                        onEvent = onEvent,
                        courseName = courseName,
                        modifier = contentModifier,
                        courseWorkId = courseWorkId,
                    )
                }

                CourseWorkType.MATERIAL -> {
                    ContentMaterial(
                        uiState = uiState,
                        onEvent = onEvent,
                        courseName = courseName,
                        modifier = contentModifier,
                        courseWorkId = courseWorkId,
                    )
                }

                else -> {
                    ContentQuestion(
                        uiState = uiState,
                        onEvent = onEvent,
                        courseName = courseName,
                        modifier = contentModifier,
                        courseWorkId = courseWorkId,
                    )
                }
            }
        }
    }

    AddLinkDialog(
        open = uiState.openAddLinkDialog,
        onDismissRequest = {
            onEvent(CreateCourseWorkUiEvent.OnOpenAddLinkDialogChange(false))
        },
        onConfirmClick = {
            onEvent(CreateCourseWorkUiEvent.AddLinkAttachment(it))
        },
    )

    AddAttachmentBottomSheet(
        show = uiState.showAddAttachmentBottomSheet,
        onDismissRequest = {
            onEvent(CreateCourseWorkUiEvent.OnShowAddAttachmentBottomSheetChange(false))
        },
        onInsertLinkClick = {
            onEvent(CreateCourseWorkUiEvent.OnOpenAddLinkDialogChange(true))
        },
        onUploadFileClick = {
            filePicker.launch("*/*")
        },
        onPickPhotoClick = {
            photoPicker.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
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

private fun Uri.handleFile(
    fileUtils: FileUtils,
    context: Context,
): CreateCourseWorkUiEvent.OnFilePicked {
    val title =
        fileUtils.getFileName(this) ?: "$lastPathSegment.${fileUtils.getFileExtension(this)}"
    val bytes = fileUtils.uriToByteArray(this)
    val file = File(context.cacheDir, title)
    file.writeBytes(bytes)
    val length =
        try {
            file.length()
        } catch (_: SecurityException) {
            null
        }
    val mimeType = fileUtils.getMimeType(this)
    return CreateCourseWorkUiEvent.OnFilePicked(file, title, mimeType, length)
}
