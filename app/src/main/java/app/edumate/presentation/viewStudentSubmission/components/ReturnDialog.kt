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
    newAssignedGrade: Int?,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
) {
    if (open) {
        val isCourseWorkGraded = (maxPoints ?: 0) > 0
        val isStudentSubmissionMarked = isCourseWorkGraded && newAssignedGrade != null
        val isStudentSubmissionMarkUpdated = assignedGrade != null && isStudentSubmissionMarked
        val isStudentNotYetSubmitted = submissionState == SubmissionState.CREATED
        val confirmButtonText =
            if (isStudentSubmissionMarkUpdated) {
                stringResource(id = R.string.update)
            } else {
                stringResource(id = R.string._return)
            }
        val (title, text) =
            when {
                isStudentNotYetSubmitted -> {
                    // Student has not yet submitted
                    stringResource(id = R.string.dialog_title_return_unsubmitted) to
                        stringResource(id = R.string.dialog_message_return_unsubmitted)
                }

                !isStudentSubmissionMarked -> {
                    // Course work is ungraded or student submission is unmarked
                    stringResource(id = R.string.dialog_title_return_without_grading) to
                        stringResource(
                            id = R.string.dialog_message_return_without_grading,
                            studentName,
                        )
                }

                isStudentSubmissionMarkUpdated -> {
                    // Course work is graded, and student submission needs updating
                    stringResource(id = R.string.dialog_title_return_update_grade) to
                        stringResource(id = R.string.dialog_message_return_update_grade)
                }

                else -> {
                    // Course work is graded, and student submission is marked
                    stringResource(id = R.string.dialog_title_return) to
                        stringResource(id = R.string.dialog_message_return)
                }
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
