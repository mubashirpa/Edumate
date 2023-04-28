package edumate.app.presentation.student_work.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import edumate.app.R.string as Strings
import edumate.app.core.DataState
import edumate.app.domain.model.User
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.student_submission.SubmissionState
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.TextAvatar
import edumate.app.presentation.student_work.StudentWorkUiEvent
import edumate.app.presentation.student_work.StudentWorkViewModel
import java.util.Date

@Composable
fun StudentWorkScreen(
    viewModel: StudentWorkViewModel = hiltViewModel(),
    courseWork: CourseWork,
    navigateToViewStudentWork: (studentWorkId: String?, assignedStudent: User) -> Unit
) {
    LaunchedEffect(courseWork.id) {
        viewModel.onEvent(StudentWorkUiEvent.OnInit(courseWork.courseId, courseWork.id))
    }

    when (val dataState = viewModel.uiState.dataState) {
        is DataState.EMPTY -> {
            ErrorScreen(
                modifier = Modifier.fillMaxSize(),
                errorMessage = dataState.message.asString()
            )
        }

        is DataState.ERROR -> {
            ErrorScreen(
                modifier = Modifier.fillMaxSize(),
                errorMessage = dataState.message.asString()
            )
        }

        DataState.LOADING -> {
            LoadingIndicator(modifier = Modifier.fillMaxSize())
        }

        DataState.SUCCESS -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 10.dp),
                content = {
                    items(viewModel.uiState.assignedStudents) { assignedStudent ->
                        val photoUrl = assignedStudent.photoUrl
                        val userId = assignedStudent.id
                        val avatar: @Composable () -> Unit = {
                            TextAvatar(
                                id = userId,
                                firstName = assignedStudent.displayName.orEmpty(),
                                lastName = ""
                            )
                        }
                        val submission =
                            viewModel.uiState.studentSubmissions.find { it.userId == userId }

                        ListItem(
                            headlineContent = {
                                Text(text = assignedStudent.displayName.orEmpty())
                            },
                            modifier = Modifier.clickable {
                                navigateToViewStudentWork(
                                    submission?.id,
                                    assignedStudent
                                )
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
                                if (submission != null) {
                                    Column {
                                        when {
                                            courseWork.maxPoints != null && courseWork.maxPoints > 0 && submission.assignedGrade != null -> {
                                                Text(
                                                    text = "${submission.assignedGrade}/${courseWork.maxPoints}",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                if (submission.late) {
                                                    Text(
                                                        text = stringResource(
                                                            id = Strings.done_late
                                                        ),
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }
                                            }

                                            submission.state == SubmissionState.TURNED_IN -> {
                                                Text(
                                                    text = stringResource(id = Strings.handed_in),
                                                    color = MaterialTheme.colorScheme.primary,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                if (submission.late) {
                                                    Text(
                                                        text = stringResource(
                                                            id = Strings.done_late
                                                        ),
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }
                                            }

                                            submission.state == SubmissionState.RETURNED -> {
                                                Icon(
                                                    imageVector = Icons.Default.Done,
                                                    contentDescription = null
                                                )
                                                if (submission.late) {
                                                    Text(
                                                        text = stringResource(
                                                            id = Strings.done_late
                                                        ),
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }
                                            }

                                            else -> {
                                                val dueDate = courseWork.dueTime
                                                if (dueDate != null && dueDate.before(Date())) {
                                                    Text(
                                                        text = stringResource(id = Strings.missing),
                                                        color = MaterialTheme.colorScheme.error,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                } else {
                                                    Text(
                                                        text = stringResource(id = Strings.assigned),
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
                                            text = stringResource(id = Strings.missing),
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    } else {
                                        Text(
                                            text = stringResource(id = Strings.assigned),
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

        DataState.UNKNOWN -> {}
    }
}