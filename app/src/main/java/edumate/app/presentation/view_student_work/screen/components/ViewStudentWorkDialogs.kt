package edumate.app.presentation.view_student_work.screen.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import edumate.app.R.string as Strings
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.presentation.view_student_work.ViewStudentWorkUiState

@Composable
fun ReturnDialog(
    onDismissRequest: () -> Unit,
    uiState: ViewStudentWorkUiState,
    courseWork: CourseWork,
    userName: String,
    onConfirmClick: () -> Unit
) {
    if (uiState.openReturnDialog) {
        val markedCourseWork = courseWork.maxPoints != null && courseWork.maxPoints > 0
        val gradedSubmission = markedCourseWork && uiState.studentWork?.assignedGrade != null
        val title = when {
            !markedCourseWork -> {
                // Course work is unmarked
                stringResource(id = Strings.dialog_return_submission_title1)
            }

            gradedSubmission -> {
                // Course work is marked and already graded student submission
                stringResource(id = Strings.dialog_return_submission_title2)
            }

            else -> {
                // Course work is marked and not graded student submission
                stringResource(id = Strings.dialog_return_submission_title3)
            }
        }
        val text = when {
            !markedCourseWork -> {
                stringResource(id = Strings.dialog_return_submission_text1, userName)
            }

            gradedSubmission -> {
                stringResource(id = Strings.dialog_return_submission_text2, userName)
            }

            else -> {
                stringResource(id = Strings.dialog_return_submission_text3, userName)
            }
        }
        val confirmButtonText = if (gradedSubmission) {
            stringResource(id = Strings.update)
        } else {
            stringResource(id = Strings._return)
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