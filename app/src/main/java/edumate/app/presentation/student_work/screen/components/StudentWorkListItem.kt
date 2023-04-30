package edumate.app.presentation.student_work.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import edumate.app.R
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.student_submissions.StudentSubmission
import edumate.app.domain.model.student_submissions.SubmissionState
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.presentation.components.TextAvatar
import java.util.Date

@Composable
fun StudentWorkListItem(
    courseWork: CourseWork,
    assignedStudent: UserProfile,
    studentSubmission: StudentSubmission?,
    onClick: (id: String?) -> Unit
) {
    val photoUrl = assignedStudent.photoUrl
    val userId = assignedStudent.id
    val avatar: @Composable () -> Unit = {
        TextAvatar(
            id = userId,
            firstName = assignedStudent.displayName.orEmpty(),
            lastName = ""
        )
    }

    ListItem(
        headlineContent = {
            Text(text = assignedStudent.displayName.orEmpty())
        },
        modifier = Modifier.clickable { onClick(studentSubmission?.id) },
        leadingContent = {
            if (photoUrl != null) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                ) {
                    when (painter.state) {
                        is AsyncImagePainter.State.Loading -> {
                            avatar()
                        }

                        is AsyncImagePainter.State.Error -> {
                            avatar()
                        }

                        else -> {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
            } else {
                avatar()
            }
        },
        trailingContent = {
            if (studentSubmission != null) {
                Column {
                    when {
                        courseWork.maxPoints != null && courseWork.maxPoints > 0 && studentSubmission.assignedGrade != null -> {
                            Text(
                                text = "${studentSubmission.assignedGrade}/${courseWork.maxPoints}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (studentSubmission.late) {
                                Text(
                                    text = stringResource(
                                        id = R.string.done_late
                                    ),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        studentSubmission.state == SubmissionState.TURNED_IN -> {
                            Text(
                                text = stringResource(
                                    id = R.string.handed_in
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (studentSubmission.late) {
                                Text(
                                    text = stringResource(
                                        id = R.string.done_late
                                    ),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        studentSubmission.state == SubmissionState.RETURNED -> {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null
                            )
                            if (studentSubmission.late) {
                                Text(
                                    text = stringResource(
                                        id = R.string.done_late
                                    ),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        else -> {
                            val dueDate = courseWork.dueTime
                            if (dueDate != null && dueDate.before(Date())) {
                                Text(
                                    text = stringResource(
                                        id = R.string.missing
                                    ),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Text(
                                    text = stringResource(
                                        id = R.string.assigned
                                    ),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            } else {
                val dueDate = courseWork.dueTime
                if (dueDate != null && dueDate.before(Date())) {
                    Text(
                        text = stringResource(id = R.string.missing),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.assigned),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    )
}