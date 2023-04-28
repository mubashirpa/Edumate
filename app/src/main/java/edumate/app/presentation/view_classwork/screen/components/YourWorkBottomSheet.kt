package edumate.app.presentation.view_classwork.screen.components

import android.text.format.DateUtils
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.core.DataState
import edumate.app.core.utils.FileType
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.student_submission.SubmissionState
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.view_classwork.ViewClassworkUiState
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourWorkBottomSheet(
    uiState: ViewClassworkUiState,
    onDismissRequest: () -> Unit,
    onAddAttachmentClick: () -> Unit,
    onRemoveAttachmentClick: (Int) -> Unit,
    onSubmitClick: () -> Unit,
    onUnSubmitClick: () -> Unit
) {
    val context = LocalContext.current
    val fileUtils = remember { FileUtils(context) }

    if (uiState.openYourWorkBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = SheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .animateContentSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = Strings.your_work),
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge
                    )
                    TopBarTrailingContent(uiState = uiState)
                }

                when (uiState.yourWorkDataState) {
                    is DataState.ERROR -> {
                        ErrorScreen(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(128.dp),
                            errorMessage = uiState.yourWorkDataState.message.asString()
                        )
                    }

                    DataState.LOADING -> {
                        LoadingIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(128.dp)
                        )
                    }

                    DataState.UNKNOWN -> {}

                    else -> {
                        Column(
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 10.dp
                            )
                        ) {
                            val attachments =
                                uiState.studentSubmissionAttachments

                            Text(
                                text = stringResource(id = Strings.attachments),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            if (attachments.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(128.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(
                                            id = Strings.you_have_no_attachments_uploaded
                                        )
                                    )
                                }
                            } else {
                                attachments.onEachIndexed { index, attachment ->
                                    OutlinedCard {
                                        ListItem(
                                            headlineContent = {
                                                Text(
                                                    text = attachment.driveFile?.title
                                                        ?: attachment.driveFile?.url.orEmpty(),
                                                    overflow = TextOverflow.Ellipsis,
                                                    maxLines = 1
                                                )
                                            },
                                            leadingContent = {
                                                val icon =
                                                    when (
                                                        fileUtils.getFileType(
                                                            attachment.driveFile?.type
                                                        )
                                                    ) {
                                                        FileType.IMAGE -> Icons.Default.Image
                                                        FileType.VIDEO -> Icons.Default.VideoFile
                                                        FileType.AUDIO -> Icons.Default.AudioFile
                                                        FileType.PDF -> Icons.Default.PictureAsPdf
                                                        FileType.UNKNOWN -> Icons.Default.InsertDriveFile
                                                    }

                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null
                                                )
                                            },
                                            trailingContent = {
                                                if (uiState.studentSubmission?.state != SubmissionState.TURNED_IN) {
                                                    IconButton(
                                                        onClick = { onRemoveAttachmentClick(index) }
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Clear,
                                                            contentDescription = null
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                    }
                                    if (index < attachments.lastIndex) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            YourWorkActionButtons(
                                uiState = uiState,
                                onAddWorkClick = onAddAttachmentClick,
                                onSubmitClick = onSubmitClick,
                                onUnSubmitClick = onUnSubmitClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YourWorkActionButtons(
    uiState: ViewClassworkUiState,
    onAddWorkClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onUnSubmitClick: () -> Unit
) {
    when (uiState.studentSubmission?.state) {
        SubmissionState.TURNED_IN -> {
            Button(
                onClick = onUnSubmitClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = Strings.unsubmit))
            }
        }

        SubmissionState.RETURNED -> {
            OutlinedButton(
                onClick = onAddWorkClick,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(id = Strings.add_work))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onSubmitClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = Strings.resubmit))
            }
        }

        SubmissionState.RECLAIMED_BY_STUDENT -> {
            val assignedGrade = uiState.studentSubmission.assignedGrade

            if (uiState.studentSubmissionAttachments.isEmpty()) {
                Button(
                    onClick = onAddWorkClick,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = Strings.add_work))
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onSubmitClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val text = if (assignedGrade != null) {
                        Strings.resubmit
                    } else {
                        Strings.mark_as_done
                    }
                    Text(text = stringResource(id = text))
                }
            } else {
                OutlinedButton(
                    onClick = onAddWorkClick,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = Strings.add_work))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onSubmitClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val text = if (assignedGrade != null) {
                        Strings.resubmit
                    } else {
                        Strings.turn_in
                    }
                    Text(text = stringResource(id = text))
                }
            }
        }

        else -> {
            if (uiState.studentSubmissionAttachments.isEmpty()) {
                Button(
                    onClick = onAddWorkClick,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = Strings.add_work))
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onSubmitClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = Strings.mark_as_done))
                }
            } else {
                OutlinedButton(
                    onClick = onAddWorkClick,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = Strings.add_work))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onSubmitClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = Strings.turn_in))
                }
            }
        }
    }
}

@Composable
private fun TopBarTrailingContent(uiState: ViewClassworkUiState) {
    val dueDate = uiState.classwork.dueTime
    val date = if (dueDate != null) {
        DateUtils.getRelativeTimeSpanString(dueDate.time)
    } else {
        null
    }

    Column {
        when (uiState.studentSubmission?.state) {
            SubmissionState.TURNED_IN -> {
                val maxPoints = uiState.classwork.maxPoints
                val assignedGrade = uiState.studentSubmission.assignedGrade

                if (maxPoints != null && maxPoints > 0 && assignedGrade != null) {
                    Text(
                        text = "$assignedGrade/$maxPoints",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = stringResource(id = Strings.turned_in),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (uiState.studentSubmission.late) {
                    Text(
                        text = stringResource(id = Strings.done_late),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            SubmissionState.RETURNED -> {
                val maxPoints = uiState.classwork.maxPoints
                val assignedGrade = uiState.studentSubmission.assignedGrade

                if (maxPoints != null && maxPoints > 0) {
                    if (assignedGrade != null) {
                        Text(
                            text = "$assignedGrade/$maxPoints",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = stringResource(id = Strings.unmarked),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null
                    )
                }
                if (uiState.studentSubmission.late) {
                    Text(
                        text = stringResource(id = Strings.done_late),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            SubmissionState.RECLAIMED_BY_STUDENT -> {
                val maxPoints = uiState.classwork.maxPoints
                val assignedGrade = uiState.studentSubmission.assignedGrade

                if (maxPoints != null && maxPoints > 0 && assignedGrade != null) {
                    Text(
                        text = "$assignedGrade/$maxPoints",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (uiState.studentSubmission.late) {
                        Text(
                            text = stringResource(id = Strings.done_late),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                } else if (dueDate != null) {
                    if (dueDate.before(Date())) {
                        Text(
                            text = stringResource(id = Strings.missing),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else if (date != null) {
                        Text(
                            text = stringResource(id = Strings.due_, date),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Text(
                        text = stringResource(id = Strings.no_due_date),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            else -> {
                if (dueDate != null) {
                    if (dueDate.before(Date())) {
                        Text(
                            text = stringResource(id = Strings.missing),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else if (date != null) {
                        Text(
                            text = stringResource(id = Strings.due_, date),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Text(
                        text = stringResource(id = Strings.no_due_date),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}