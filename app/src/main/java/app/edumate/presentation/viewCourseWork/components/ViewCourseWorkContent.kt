package app.edumate.presentation.viewCourseWork.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.ext.header
import app.edumate.core.utils.FileType
import app.edumate.core.utils.FileUtils
import app.edumate.domain.model.courseWork.CourseWork
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.domain.model.studentSubmission.SubmissionState
import app.edumate.presentation.components.AttachmentsListItem
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.viewCourseWork.ViewCourseWorkUiEvent
import app.edumate.presentation.viewCourseWork.ViewCourseWorkUiState
import java.io.File
import kotlin.collections.forEach
import kotlin.collections.orEmpty

@Composable
fun ViewCourseWorkContent(
    uiState: ViewCourseWorkUiState,
    onEvent: (ViewCourseWorkUiEvent) -> Unit,
    courseWork: CourseWork,
    isCurrentUserTeacher: Boolean,
    fileUtils: FileUtils,
    onNavigateToImageViewer: (url: String, title: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val dueTime = courseWork.dueTime
    val maxPoints = courseWork.maxPoints
    val description = courseWork.description
    val attachments = courseWork.materials
    val workType = courseWork.workType
    val contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { uri ->
                onEvent(uri.handleFile(fileUtils, context))
            }
        }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        content = {
            header {
                Column {
                    Spacer(modifier = Modifier.height(6.dp))
                    dueTime?.let {
                        CourseWorkDueText(dueTime)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    Text(
                        text = courseWork.title.orEmpty(),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    if (maxPoints != null && maxPoints > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(id = R.string._points, maxPoints),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            description?.let {
                header {
                    Text(
                        text = description,
                        modifier = Modifier.padding(top = 14.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            if (!attachments.isNullOrEmpty()) {
                header {
                    Text(
                        text = stringResource(id = R.string.attachments),
                        modifier = Modifier.padding(top = 14.dp, bottom = 6.dp),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                items(attachments) { material ->
                    AttachmentsListItem(
                        material = material,
                        fileUtils = fileUtils,
                        onClickFile = { mimeType, url, title ->
                            if (mimeType == FileType.IMAGE) {
                                onNavigateToImageViewer(url, title)
                            } else {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(browserIntent)
                            }
                        },
                        onClickLink = { url ->
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(browserIntent)
                        },
                    )
                }
            }
            if (!isCurrentUserTeacher) {
                when (workType) {
                    CourseWorkType.ASSIGNMENT -> {
                        header {
                            Button(
                                onClick = {
                                    onEvent(
                                        ViewCourseWorkUiEvent.OnShowStudentSubmissionBottomSheetChange(
                                            true,
                                        ),
                                    )
                                },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 14.dp),
                            ) {
                                Text(text = stringResource(id = R.string.your_work))
                            }
                        }
                    }

                    CourseWorkType.MULTIPLE_CHOICE_QUESTION -> {
                        multipleChoiceContent(
                            uiState = uiState,
                            onEvent = onEvent,
                            modifier = Modifier.padding(top = 14.dp),
                            courseWork = courseWork,
                        )
                    }

                    CourseWorkType.SHORT_ANSWER_QUESTION -> {
                        shortAnswerContent(
                            uiState = uiState,
                            onEvent = onEvent,
                            modifier = Modifier.padding(top = 14.dp),
                            courseWork = courseWork,
                        )
                    }

                    else -> Unit
                }
            }
        },
    )

    StudentSubmissionBottomSheet(
        show = uiState.showStudentSubmissionBottomSheet,
        courseWork = courseWork,
        studentSubmissionResult = uiState.studentSubmissionResult,
        attachments = uiState.assignmentAttachments,
        onDismissRequest = {
            onEvent(ViewCourseWorkUiEvent.OnShowStudentSubmissionBottomSheetChange(false))
        },
        onAddAttachmentClick = {
            filePicker.launch("*/*")
        },
        onRemoveAttachmentClick = {
            onEvent(ViewCourseWorkUiEvent.OnOpenRemoveAttachmentDialogChange(it))
        },
        onSubmitClick = {
            onEvent(ViewCourseWorkUiEvent.OnOpenTurnInDialogChange(true))
        },
        onUnSubmitClick = {
            onEvent(ViewCourseWorkUiEvent.OnOpenUnSubmitDialogChange(true))
        },
        onRetryClick = {
            onEvent(ViewCourseWorkUiEvent.Retry)
        },
    )

    TurnInDialog(
        open = uiState.openTurnInDialog,
        courseWorkTitle = courseWork.title.orEmpty(),
        isQuestion = workType == CourseWorkType.MULTIPLE_CHOICE_QUESTION || workType == CourseWorkType.SHORT_ANSWER_QUESTION,
        submissionState = uiState.studentSubmissionResult.data?.state ?: SubmissionState.CREATED,
        studentSubmissionAttachmentsSize = uiState.assignmentAttachments.size,
        onDismissRequest = {
            onEvent(ViewCourseWorkUiEvent.OnOpenTurnInDialogChange(false))
        },
        onConfirmButtonClick = {
            onEvent(ViewCourseWorkUiEvent.TurnIn(courseWork.workType))
        },
    )

    UnSubmitDialog(
        open = uiState.openUnSubmitDialog,
        onDismissRequest = {
            onEvent(ViewCourseWorkUiEvent.OnOpenUnSubmitDialogChange(false))
        },
        onConfirmButtonClick = {
            onEvent(ViewCourseWorkUiEvent.Reclaim)
        },
    )

    RemoveAttachmentDialog(
        open = uiState.removeAttachmentIndex != null,
        onDismissRequest = {
            onEvent(ViewCourseWorkUiEvent.OnOpenRemoveAttachmentDialogChange(null))
        },
        onConfirmButtonClick = {
            onEvent(ViewCourseWorkUiEvent.RemoveAttachment(uiState.removeAttachmentIndex!!))
        },
    )
}

private fun LazyGridScope.shortAnswerContent(
    uiState: ViewCourseWorkUiState,
    onEvent: (ViewCourseWorkUiEvent) -> Unit,
    courseWork: CourseWork,
    modifier: Modifier = Modifier,
) {
    header {
        val focusRequester = remember { FocusRequester() }

        OutlinedCard(modifier = modifier) {
            when (val studentSubmissionResult = uiState.studentSubmissionResult) {
                is Result.Empty -> {}

                is Result.Error -> {
                    ErrorScreen(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .heightIn(min = 128.dp),
                        errorMessage = studentSubmissionResult.message!!.asString(),
                        onRetryClick = {
                            onEvent(ViewCourseWorkUiEvent.Retry)
                        },
                    )
                }

                is Result.Loading -> {
                    LoadingScreen(modifier = Modifier.height(128.dp))
                }

                is Result.Success -> {
                    val studentSubmission = studentSubmissionResult.data

                    if (studentSubmission != null) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(id = R.string.your_answer),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            DueText(
                                courseWork = courseWork,
                                studentSubmission = studentSubmission,
                            )
                        }
                        if (studentSubmission.shortAnswerSubmission?.answer == null || uiState.editShortAnswer) {
                            TextField(
                                state = uiState.shortAnswer,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 12.dp)
                                        .focusRequester(focusRequester),
                                label = {
                                    Text(text = stringResource(id = R.string.type_your_answer))
                                },
                                keyboardOptions =
                                    KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Sentences,
                                        autoCorrectEnabled = true,
                                    ),
                            )
                            Button(
                                onClick = {
                                    onEvent(ViewCourseWorkUiEvent.TurnIn(courseWork.workType))
                                },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                enabled =
                                    uiState.shortAnswer.text
                                        .trim()
                                        .isNotEmpty() &&
                                        uiState.shortAnswer.text != studentSubmission.shortAnswerSubmission?.answer,
                            ) {
                                Text(text = stringResource(id = R.string.turn_in))
                            }

                            LaunchedEffect(true) {
                                focusRequester.requestFocus()
                            }
                        } else {
                            Text(
                                text = studentSubmission.shortAnswerSubmission.answer,
                                modifier =
                                    Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 12.dp),
                            )
                            OutlinedButton(
                                onClick = {
                                    onEvent(ViewCourseWorkUiEvent.OnEditShortAnswerChange(true))
                                },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                            ) {
                                Text(text = stringResource(id = R.string.edit))
                            }
                        }
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
    }
}

private fun LazyGridScope.multipleChoiceContent(
    uiState: ViewCourseWorkUiState,
    onEvent: (ViewCourseWorkUiEvent) -> Unit,
    courseWork: CourseWork,
    modifier: Modifier = Modifier,
) {
    header {
        OutlinedCard(modifier = modifier) {
            when (val studentSubmissionResult = uiState.studentSubmissionResult) {
                is Result.Empty -> {}

                is Result.Error -> {
                    ErrorScreen(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .heightIn(min = 128.dp),
                        errorMessage = studentSubmissionResult.message!!.asString(),
                        onRetryClick = {
                            onEvent(ViewCourseWorkUiEvent.Retry)
                        },
                    )
                }

                is Result.Loading -> {
                    LoadingScreen(modifier = Modifier.height(128.dp))
                }

                is Result.Success -> {
                    val studentSubmission = studentSubmissionResult.data

                    if (studentSubmission != null) {
                        val choices = courseWork.multipleChoiceQuestion?.choices.orEmpty()
                        val choiceSelectable =
                            studentSubmission.multipleChoiceSubmission?.answer == null

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(id = R.string.your_answer),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            DueText(
                                courseWork = courseWork,
                                studentSubmission = studentSubmission,
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(modifier = Modifier.selectableGroup()) {
                            choices.forEach { text ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .selectable(
                                            selected = (text == uiState.multipleChoiceAnswer),
                                            enabled = choiceSelectable,
                                            role = Role.RadioButton,
                                            onClick = {
                                                onEvent(
                                                    ViewCourseWorkUiEvent.OnMultipleChoiceAnswerValueChange(
                                                        text,
                                                    ),
                                                )
                                            },
                                        ).padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    RadioButton(
                                        selected = (text == uiState.multipleChoiceAnswer),
                                        onClick = null, // null recommended for accessibility with screen readers
                                    )
                                    Text(
                                        text = text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 16.dp),
                                    )
                                }
                            }
                        }
                        if (choiceSelectable) {
                            Button(
                                onClick = {
                                    onEvent(ViewCourseWorkUiEvent.OnOpenTurnInDialogChange(true))
                                },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                enabled = uiState.multipleChoiceAnswer.isNotEmpty(),
                            ) {
                                Text(text = stringResource(id = R.string.turn_in))
                            }
                        }
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
    }
}

private fun Uri.handleFile(
    fileUtils: FileUtils,
    context: Context,
): ViewCourseWorkUiEvent.OnFilePicked {
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
    return ViewCourseWorkUiEvent.OnFilePicked(file, title, mimeType, length)
}
