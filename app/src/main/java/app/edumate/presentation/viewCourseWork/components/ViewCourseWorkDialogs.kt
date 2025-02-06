package app.edumate.presentation.viewCourseWork.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import app.edumate.R
import app.edumate.domain.model.studentSubmission.SubmissionState

@Composable
fun TurnInDialog(
    open: Boolean,
    courseWorkTitle: String,
    isQuestion: Boolean,
    submissionState: SubmissionState,
    studentSubmissionAttachmentsSize: Int,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
) {
    if (open) {
        val hasAttachments = studentSubmissionAttachmentsSize > 0
        val title: String
        val text: String

        when {
            isQuestion -> {
                title = stringResource(id = R.string.dialog_title_turn_in_question)
                text = stringResource(id = R.string.dialog_message_turn_in_question)
            }

            hasAttachments -> {
                title = stringResource(id = R.string.dialog_title_turn_in)
                text =
                    pluralStringResource(
                        id = R.plurals.dialog_message_turn_in,
                        count = studentSubmissionAttachmentsSize,
                        studentSubmissionAttachmentsSize,
                        courseWorkTitle,
                    )
            }

            else -> {
                title = stringResource(id = R.string.dialog_title_turn_in_empty_attachments)
                text = stringResource(id = R.string.dialog_message_turn_in, courseWorkTitle)
            }
        }

        val confirmButtonText =
            if (submissionState == SubmissionState.RECLAIMED_BY_STUDENT || submissionState == SubmissionState.RETURNED) {
                stringResource(id = R.string.resubmit)
            } else {
                val buttonTextId =
                    if (hasAttachments) {
                        R.string.turn_in
                    } else {
                        R.string.mark_as_done
                    }
                stringResource(id = buttonTextId)
            }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = title)
            },
            text = {
                Text(text = text)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        onConfirmButtonClick()
                    },
                ) {
                    Text(text = confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
        )
    }
}

@Composable
fun UnSubmitDialog(
    open: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
) {
    if (open) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = R.string.dialog_title_unsubmit))
            },
            text = {
                Text(text = stringResource(id = R.string.dialog_message_unsubmit))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        onConfirmButtonClick()
                    },
                ) {
                    Text(stringResource(id = R.string.unsubmit))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
        )
    }
}

@Composable
fun RemoveAttachmentDialog(
    open: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
) {
    if (open) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = R.string.dialog_title_remove_attachment))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        onConfirmButtonClick()
                    },
                ) {
                    Text(stringResource(id = R.string.remove))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
        )
    }
}
