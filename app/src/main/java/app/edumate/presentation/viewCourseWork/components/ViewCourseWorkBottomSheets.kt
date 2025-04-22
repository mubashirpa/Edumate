package app.edumate.presentation.viewCourseWork.components

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.core.utils.FileType
import app.edumate.domain.model.courseWork.CourseWork
import app.edumate.domain.model.material.Material
import app.edumate.domain.model.studentSubmission.StudentSubmission
import app.edumate.domain.model.studentSubmission.SubmissionState
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentSubmissionBottomSheet(
    show: Boolean,
    courseWork: CourseWork,
    studentSubmissionResult: Result<StudentSubmission>,
    attachments: List<Material>,
    userMessage: UiText?,
    onDismissRequest: () -> Unit,
    onAddAttachmentClick: () -> Unit,
    onRemoveAttachmentClick: (Int) -> Unit,
    onSubmitClick: () -> Unit,
    onUnSubmitClick: () -> Unit,
    onRetryClick: () -> Unit,
    onFileAttachmentClick: (mimeType: FileType, url: String, title: String?) -> Unit,
) {
    if (show) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val context = LocalContext.current
        val scrollState = rememberScrollState()
        val snackbarHostState = remember { SnackbarHostState() }
        val isFullscreen =
            bottomSheetState.targetValue == SheetValue.Expanded &&
                (scrollState.canScrollForward || scrollState.canScrollBackward)
        val cornerSize by animateDpAsState(
            targetValue = if (isFullscreen) 0.dp else 28.dp,
            label = stringResource(id = R.string.label_animate_bottom_sheet_corner_size),
        )
        val paddingValues = WindowInsets.systemBars.asPaddingValues()

        userMessage?.let { userMessage ->
            LaunchedEffect(userMessage) {
                snackbarHostState.showSnackbar(userMessage.asString(context))
                // The message will be dismissed from root screen
            }
        }

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
            shape = RoundedCornerShape(topStart = cornerSize, topEnd = cornerSize),
            dragHandle = {
                val topPadding by animateDpAsState(
                    targetValue = if (isFullscreen) paddingValues.calculateTopPadding() else 0.dp,
                    label = stringResource(id = R.string.label_animate_bottom_sheet_top_padding),
                )
                BottomSheetDefaults.DragHandle(modifier = Modifier.padding(top = topPadding))
            },
        ) {
            Box {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .animateContentSize(),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(id = R.string.your_work),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        studentSubmissionResult.data?.let { studentSubmission ->
                            DueText(
                                courseWork = courseWork,
                                studentSubmission = studentSubmission,
                            )
                        }
                    }
                    when (studentSubmissionResult) {
                        is Result.Empty -> {}

                        is Result.Error -> {
                            ErrorScreen(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 128.dp),
                                errorMessage = studentSubmissionResult.message!!.asString(),
                                onRetryClick = onRetryClick,
                            )
                        }

                        is Result.Loading -> {
                            LoadingScreen(modifier = Modifier.height(128.dp))
                        }

                        is Result.Success -> {
                            val studentSubmission = studentSubmissionResult.data

                            if (studentSubmission != null) {
                                StudentSubmissionBottomSheetContent(
                                    bottomSheetState = bottomSheetState,
                                    studentSubmission = studentSubmission,
                                    attachments = attachments,
                                    onAddAttachmentClick = onAddAttachmentClick,
                                    onRemoveAttachmentClick = onRemoveAttachmentClick,
                                    onSubmitClick = onSubmitClick,
                                    onUnSubmitClick = onUnSubmitClick,
                                    onFileAttachmentClick = onFileAttachmentClick,
                                    modifier =
                                        Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 12.dp,
                                        ),
                                )
                            } else {
                                ErrorScreen(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(128.dp),
                                )
                            }
                        }
                    }
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentSubmissionBottomSheetContent(
    bottomSheetState: SheetState,
    studentSubmission: StudentSubmission,
    attachments: List<Material>,
    onAddAttachmentClick: () -> Unit,
    onRemoveAttachmentClick: (Int) -> Unit,
    onSubmitClick: () -> Unit,
    onUnSubmitClick: () -> Unit,
    onFileAttachmentClick: (mimeType: FileType, url: String, title: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.attachments),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (attachments.isEmpty()) {
            ErrorScreen(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(128.dp),
                errorMessage = stringResource(id = R.string.you_have_no_attachments_uploaded),
            )
        } else {
            attachments.onEachIndexed { index, attachment ->
                AttachmentsListItem(
                    material = attachment,
                    submissionState = studentSubmission.state,
                    onClickFile = { mimeType, link, title ->
                        if (mimeType == FileType.IMAGE || mimeType == FileType.PDF) {
                            coroutineScope
                                .launch { bottomSheetState.hide() }
                                .invokeOnCompletion {
                                    if (!bottomSheetState.isVisible) {
                                        onFileAttachmentClick(
                                            mimeType,
                                            link,
                                            title,
                                        )
                                    }
                                }
                        } else {
                            onFileAttachmentClick(
                                mimeType,
                                link,
                                title,
                            )
                        }
                    },
                    onClickLink = { url ->
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, url.toUri())
                        context.startActivity(browserIntent)
                    },
                    onRemoveAttachmentClick = {
                        onRemoveAttachmentClick(index)
                    },
                )
                if (index < attachments.lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        StudentSubmissionActionButtons(
            studentSubmission = studentSubmission,
            attachments = attachments,
            onAddWorkClick = onAddAttachmentClick,
            onSubmitClick = onSubmitClick,
            onUnSubmitClick = onUnSubmitClick,
        )
    }
}

@Composable
private fun StudentSubmissionActionButtons(
    studentSubmission: StudentSubmission,
    attachments: List<Material>,
    onAddWorkClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onUnSubmitClick: () -> Unit,
) {
    val maxWidthModifier = Modifier.fillMaxWidth()
    val addWorkButton: @Composable () -> Unit = {
        Button(
            onClick = onAddWorkClick,
            modifier = maxWidthModifier,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.add_work))
        }
    }

    Column {
        when (studentSubmission.state) {
            SubmissionState.TURNED_IN -> {
                Button(
                    onClick = onUnSubmitClick,
                    modifier = maxWidthModifier,
                ) {
                    Text(text = stringResource(id = R.string.unsubmit))
                }
            }

            SubmissionState.RETURNED -> {
                addWorkButton()
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onSubmitClick,
                    modifier = maxWidthModifier,
                ) {
                    Text(text = stringResource(id = R.string.resubmit))
                }
            }

            SubmissionState.RECLAIMED_BY_STUDENT -> {
                val text =
                    when {
                        studentSubmission.assignedGrade != null -> R.string.resubmit
                        attachments.isEmpty() -> R.string.mark_as_done
                        else -> R.string.turn_in
                    }

                addWorkButton()
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onSubmitClick,
                    modifier = maxWidthModifier,
                ) {
                    Text(text = stringResource(id = text))
                }
            }

            else -> {
                val text =
                    if (attachments.isEmpty()) {
                        R.string.mark_as_done
                    } else {
                        R.string.turn_in
                    }

                addWorkButton()
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onSubmitClick,
                    modifier = maxWidthModifier,
                ) {
                    Text(text = stringResource(id = text))
                }
            }
        }
    }
}
