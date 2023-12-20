package edumate.app.presentation.student_work.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.student_submissions.StudentSubmission
import edumate.app.domain.model.student_submissions.SubmissionState
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.presentation.components.UserAvatar
import java.util.Date
import edumate.app.R.string as Strings

@Composable
fun StudentWorkListItem(
    courseWork: CourseWork,
    assignedStudent: UserProfile,
    studentSubmission: StudentSubmission?,
    onClick: (id: String?) -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(text = assignedStudent.displayName.orEmpty())
        },
        modifier = Modifier.clickable { onClick(studentSubmission?.id) },
        leadingContent = {
            UserAvatar(
                id = assignedStudent.id,
                fullName = assignedStudent.displayName ?: assignedStudent.emailAddress.orEmpty(),
                photoUrl = assignedStudent.photoUrl,
            )
        },
        trailingContent = {
            if (studentSubmission != null) {
                Column {
                    when {
                        courseWork.maxPoints != null && courseWork.maxPoints > 0 && studentSubmission.assignedGrade != null -> {
                            Text(
                                text = "${studentSubmission.assignedGrade}/${courseWork.maxPoints}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            if (studentSubmission.state == SubmissionState.CREATED || studentSubmission.state == SubmissionState.NEW) {
                                Text(
                                    text = stringResource(id = Strings.not_handed_in),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            } else if (studentSubmission.late) {
                                Text(
                                    text = stringResource(id = Strings.done_late),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }

                        studentSubmission.state == SubmissionState.TURNED_IN -> {
                            Text(
                                text = stringResource(id = Strings.handed_in),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            if (studentSubmission.late) {
                                Text(
                                    text = stringResource(id = Strings.done_late),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }

                        studentSubmission.state == SubmissionState.RETURNED -> {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                            )
                            if (studentSubmission.late) {
                                Text(
                                    text = stringResource(id = Strings.done_late),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }

                        else -> {
                            val dueDate = courseWork.dueTime
                            if (dueDate != null && dueDate.before(Date())) {
                                Text(
                                    text = stringResource(id = Strings.missing),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            } else {
                                Text(
                                    text = stringResource(id = Strings.assigned),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }
            } else {
                val dueDate = courseWork.dueTime
                if (dueDate != null && dueDate.before(Date())) {
                    Text(
                        text = stringResource(id = Strings.missing),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    Text(
                        text = stringResource(id = Strings.assigned),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        },
    )
}
