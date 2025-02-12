package app.edumate.presentation.courseWork.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.edumate.R
import app.edumate.domain.model.courseWork.CourseWorkType

@Composable
fun DeleteCourseWorkDialog(
    workType: CourseWorkType?,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
) {
    if (workType != null) {
        val title: String
        val message: String

        when (workType) {
            CourseWorkType.ASSIGNMENT -> {
                title = stringResource(id = R.string.dialog_title_delete_assignment)
                message = stringResource(id = R.string.dialog_message_delete_coursework)
            }

            CourseWorkType.MATERIAL -> {
                title = stringResource(id = R.string.dialog_title_delete_material)
                message = stringResource(id = R.string.dialog_message_delete_material)
            }

            CourseWorkType.MULTIPLE_CHOICE_QUESTION -> {
                title = stringResource(id = R.string.dialog_title_delete_question)
                message = stringResource(id = R.string.dialog_message_delete_coursework)
            }

            CourseWorkType.SHORT_ANSWER_QUESTION -> {
                title = stringResource(id = R.string.dialog_title_delete_question)
                message = stringResource(id = R.string.dialog_message_delete_coursework)
            }
        }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = title)
            },
            text = {
                Text(text = message)
            },
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) {
                    Text(stringResource(id = R.string.delete))
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
