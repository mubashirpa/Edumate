package app.edumate.presentation.studentWork.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.domain.model.courseWork.CourseWork
import app.edumate.domain.model.studentSubmission.StudentSubmission
import app.edumate.domain.model.studentSubmission.SubmissionState
import app.edumate.presentation.components.UserAvatar
import kotlinx.datetime.Instant
import kotlinx.datetime.isDistantPast

@Composable
fun StudentWorkListItem(
    courseWork: CourseWork,
    studentSubmission: StudentSubmission,
    onClick: (studentId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val student = studentSubmission.user
    val studentName = student?.name.orEmpty()
    val studentId = student?.id

    ListItem(
        headlineContent = {
            Text(text = studentName)
        },
        modifier =
            modifier.clickable {
                studentId?.let(onClick)
            },
        leadingContent = {
            UserAvatar(
                id = studentId.orEmpty(),
                fullName = studentName,
                photoUrl = student?.photoUrl,
            )
        },
        trailingContent = {
            DueText(
                courseWork = courseWork,
                studentSubmission = studentSubmission,
            )
        },
    )
}

@Composable
private fun DueText(
    courseWork: CourseWork,
    studentSubmission: StudentSubmission,
    modifier: Modifier = Modifier,
) {
    val maxPoints = courseWork.maxPoints
    val submissionState = studentSubmission.state
    val assignedGrade = studentSubmission.assignedGrade
    val draftGrade = studentSubmission.draftGrade
    val dueDateTime =
        remember {
            courseWork.dueTime?.let { dueTime ->
                Instant.parse(dueTime)
            }
        }
    val isLate = studentSubmission.late == true
    val late: @Composable (isLate: Boolean) -> Unit = { isLate ->
        if (isLate) {
            Text(
                text = stringResource(id = R.string.done_late),
                modifier = Modifier.padding(top = 2.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
    ) {
        if (maxPoints != null && maxPoints > 0) {
            when {
                draftGrade != null -> {
                    val text =
                        assignedGrade?.let {
                            stringResource(id = R.string.previously_, assignedGrade, maxPoints)
                        } ?: stringResource(id = R.string.draft)

                    Text(
                        text = "$draftGrade/$maxPoints",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                assignedGrade != null -> {
                    /* TODO: Show "Not turned in" text if the teacher returned the student
                       submission without the student turning in at least once. Determine if
                       the student has not turned in at least once based on submissionHistory. */

                    val annotatedString =
                        buildAnnotatedString {
                            append("$assignedGrade")
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                                append("/$maxPoints")
                            }
                        }

                    Text(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    late(isLate)
                }

                submissionState == SubmissionState.TURNED_IN -> {
                    Text(
                        text = stringResource(id = R.string.turned_in),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    late(isLate)
                }

                dueDateTime != null && dueDateTime.isDistantPast -> {
                    Text(
                        text = stringResource(id = R.string.missing),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                else -> {
                    Text(
                        text = stringResource(id = R.string.assigned),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        } else {
            when {
                submissionState == SubmissionState.TURNED_IN -> {
                    Text(
                        text = stringResource(id = R.string.turned_in),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    late(isLate)
                }

                submissionState == SubmissionState.RETURNED -> {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                    )
                    late(isLate)
                }

                dueDateTime != null && dueDateTime.isDistantPast -> {
                    Text(
                        text = stringResource(id = R.string.missing),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                else -> {
                    Text(
                        text = stringResource(id = R.string.assigned),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
