package edumate.app.presentation.classwork.screen.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import edumate.app.R.string as Strings
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.course_work.CourseWorkType

@Composable
fun DeleteClassworkDialog(
    onDismissRequest: () -> Unit,
    classwork: CourseWork?,
    onConfirmClick: (workId: String) -> Unit
) {
    if (classwork != null) {
        val title = when (classwork.workType) {
            CourseWorkType.MATERIAL -> stringResource(id = Strings.delete_material)
            CourseWorkType.ASSIGNMENT -> stringResource(id = Strings.delete_assignment)
            CourseWorkType.SHORT_ANSWER_QUESTION -> stringResource(id = Strings.delete_question)
            CourseWorkType.MULTIPLE_CHOICE_QUESTION -> stringResource(id = Strings.delete_question)
            else -> stringResource(id = Strings.delete)
        }
        val message = when (classwork.workType) {
            CourseWorkType.MATERIAL -> stringResource(id = Strings.comments_will_also_be_deleted)
            CourseWorkType.ASSIGNMENT -> stringResource(
                id = Strings.marks_and_comments_will_also_be_deleted
            )

            CourseWorkType.SHORT_ANSWER_QUESTION -> stringResource(
                id = Strings.marks_and_comments_will_also_be_deleted
            )

            CourseWorkType.MULTIPLE_CHOICE_QUESTION -> stringResource(
                id = Strings.marks_and_comments_will_also_be_deleted
            )

            else -> ""
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
                TextButton(onClick = { onConfirmClick(classwork.id) }) {
                    Text(stringResource(id = Strings.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = Strings.cancel))
                }
            }
        )
    }
}