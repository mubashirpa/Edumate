package edumate.app.presentation.student_work.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.student_submission.SubmissionState
import edumate.app.presentation.components.TextAvatar
import edumate.app.presentation.student_work.StudentWorkUiEvent
import edumate.app.presentation.student_work.StudentWorkViewModel
import java.util.Date

@Composable
fun StudentWorkScreen(
    viewModel: StudentWorkViewModel = hiltViewModel(),
    courseWork: CourseWork
) {
    LaunchedEffect(courseWork.id) {
        viewModel.onEvent(StudentWorkUiEvent.OnInit(courseWork.courseId, courseWork.id))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 10.dp),
        content = {
            items(viewModel.uiState.assignedStudents) { assignedStudents ->
                val photoUrl = assignedStudents.photoUrl
                val userId = assignedStudents.id
                val avatar: @Composable () -> Unit = {
                    TextAvatar(
                        id = userId,
                        firstName = assignedStudents.displayName.orEmpty(),
                        lastName = ""
                    )
                }

                ListItem(
                    headlineContent = {
                        Text(text = assignedStudents.displayName.orEmpty())
                    },
                    modifier = Modifier.clickable {
                        // TODO("Not yet implemented")
                    },
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
                        val submission =
                            viewModel.uiState.studentSubmissions.find { it.userId == userId }

                        if (submission != null) {
                            if (submission.state == SubmissionState.TURNED_IN) {
                                Column {
                                    Text(
                                        text = "Handed in",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (submission.late) {
                                        Text(
                                            text = "Done late",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            } else if (submission.assignedGrade != null && courseWork.maxPoints != null) {
                                Column {
                                    Text(
                                        text = "${submission.assignedGrade}/${courseWork.maxPoints}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (submission.late) {
                                        Text(
                                            text = "Done late",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            } else {
                                val dueDate = courseWork.dueTime
                                if (dueDate != null && dueDate.before(Date())) {
                                    Text(
                                        text = "Missing",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                } else {
                                    Text(
                                        text = "Assigned",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        } else {
                            val dueDate = courseWork.dueTime
                            if (dueDate != null && dueDate.before(Date())) {
                                Text(
                                    text = "Missing",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Text(
                                    text = "Assigned",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                )
            }
        }
    )
}