package app.edumate.presentation.viewStudentSubmission.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.edumate.R
import app.edumate.domain.model.studentSubmission.SubmissionState

@Composable
fun ReturnDialog(
    open: Boolean,
    studentName: String,
    maxPoints: Int?,
    submissionState: SubmissionState?,
    assignedGrade: Int?,
    draftGrade: Int?,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
) {
    if (open) {
        val isCourseWorkGraded = (maxPoints ?: 0) > 0
        val isStudentSubmissionMarked = isCourseWorkGraded && draftGrade != null && draftGrade > 0
        val isStudentSubmissionMarkUpdated = assignedGrade != null && isStudentSubmissionMarked
        val isStudentNotYetSubmitted = submissionState == SubmissionState.CREATED
        val (title, text, confirmButtonText) =
            getDialogContent(
                isStudentNotYetSubmitted,
                isStudentSubmissionMarked,
                isStudentSubmissionMarkUpdated,
                studentName,
            )

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
                    Text(confirmButtonText)
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
private fun getDialogContent(
    isStudentNotYetSubmitted: Boolean,
    isStudentSubmissionMarked: Boolean,
    isStudentSubmissionMarkUpdated: Boolean,
    studentName: String,
): Triple<String, String, String> =
    when {
        isStudentNotYetSubmitted -> {
            // Student has not yet submitted
            Triple(
                stringResource(id = R.string.dialog_title_return_unsubmitted),
                stringResource(id = R.string.dialog_message_return_unsubmitted),
                stringResource(id = R.string._return),
            )
        }

        !isStudentSubmissionMarked -> {
            // Course work is ungraded or student submission is unmarked
            Triple(
                stringResource(id = R.string.dialog_title_return_without_grading),
                stringResource(id = R.string.dialog_message_return_without_grading, studentName),
                stringResource(id = R.string._return),
            )
        }

        isStudentSubmissionMarkUpdated -> {
            // Course work is graded, and student submission needs updating
            Triple(
                stringResource(id = R.string.dialog_title_return_update_grade),
                stringResource(id = R.string.dialog_message_return_update_grade),
                stringResource(id = R.string.update),
            )
        }

        else -> {
            // Course work is graded, and student submission is marked
            Triple(
                stringResource(id = R.string.dialog_title_return),
                stringResource(id = R.string.dialog_message_return),
                stringResource(id = R.string._return),
            )
        }
    }
