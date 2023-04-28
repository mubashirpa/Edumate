package edumate.app.presentation.view_classwork.screen.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import edumate.app.R.string as Strings
import edumate.app.domain.model.student_submission.SubmissionState
import edumate.app.presentation.view_classwork.ViewClassworkUiState

@Composable
fun TurnInDialog(
    uiState: ViewClassworkUiState,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {
    if (uiState.openTurnInDialog) {
        val text = if (uiState.studentSubmissionAttachments.isNotEmpty()) {
            stringResource(
                id = Strings.dialog_turn_in_assignment_text1,
                uiState.studentSubmissionAttachments.size,
                uiState.classwork.title
            )
        } else {
            stringResource(
                id = Strings.dialog_turn_in_assignment_text2,
                uiState.classwork.title
            )
        }
        val confirmButtonText =
            if (uiState.studentSubmission?.state == SubmissionState.RECLAIMED_BY_STUDENT || uiState.studentSubmission?.state == SubmissionState.RETURNED) {
                stringResource(id = Strings.resubmit)
            } else {
                if (uiState.studentSubmissionAttachments.isEmpty()) {
                    stringResource(id = Strings.mark_as_done)
                } else {
                    stringResource(id = Strings.turn_in)
                }
            }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = Strings.dialog_turn_in_assignment_title))
            },
            text = {
                Text(text = text)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        onConfirmClick()
                    }
                ) { Text(confirmButtonText) }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = Strings.cancel))
                }
            }
        )
    }
}

@Composable
fun UnSubmitDialog(
    uiState: ViewClassworkUiState,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {
    if (uiState.openUnSubmitDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = Strings.dialog_unsubmit_assignment_title))
            },
            text = {
                Text(text = stringResource(id = Strings.dialog_unsubmit_assignment_text))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        onConfirmClick()
                    }
                ) { Text(stringResource(id = Strings.unsubmit)) }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = Strings.cancel))
                }
            }
        )
    }
}

@Composable
fun RemoveAttachmentDialog(
    uiState: ViewClassworkUiState,
    onDismissRequest: () -> Unit,
    onConfirmClick: (attachmentIndex: Int) -> Unit
) {
    if (uiState.openRemoveAttachmentDialog != null) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            text = {
                Text(text = stringResource(id = Strings.dialog_remove_attachment_text))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        onConfirmClick(uiState.openRemoveAttachmentDialog)
                    }
                ) { Text(stringResource(id = Strings.remove)) }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = Strings.cancel))
                }
            }
        )
    }
}