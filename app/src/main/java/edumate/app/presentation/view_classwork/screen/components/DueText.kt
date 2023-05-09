package edumate.app.presentation.view_classwork.screen.components

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import edumate.app.R.string as Strings
import edumate.app.domain.model.student_submissions.SubmissionState
import edumate.app.presentation.view_classwork.ViewClassworkUiState
import java.util.Date

@Composable
fun DueText(uiState: ViewClassworkUiState) {
    val dueDate = uiState.classwork.dueTime
    val date = if (dueDate != null) {
        DateUtils.getRelativeTimeSpanString(dueDate.time)
    } else {
        null
    }

    Column {
        when (uiState.studentSubmission?.state) {
            SubmissionState.TURNED_IN -> {
                val maxPoints = uiState.classwork.maxPoints
                val assignedGrade = uiState.studentSubmission.assignedGrade

                if (maxPoints != null && maxPoints > 0 && assignedGrade != null) {
                    Text(
                        text = "$assignedGrade/$maxPoints",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = stringResource(id = Strings.turned_in),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (uiState.studentSubmission.late) {
                    Text(
                        text = stringResource(id = Strings.done_late),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            SubmissionState.RETURNED -> {
                val maxPoints = uiState.classwork.maxPoints
                val assignedGrade = uiState.studentSubmission.assignedGrade

                if (maxPoints != null && maxPoints > 0) {
                    if (assignedGrade != null) {
                        Text(
                            text = "$assignedGrade/$maxPoints",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = stringResource(id = Strings.unmarked),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null
                    )
                }
                if (uiState.studentSubmission.late) {
                    Text(
                        text = stringResource(id = Strings.done_late),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            SubmissionState.RECLAIMED_BY_STUDENT -> {
                val maxPoints = uiState.classwork.maxPoints
                val assignedGrade = uiState.studentSubmission.assignedGrade

                if (maxPoints != null && maxPoints > 0 && assignedGrade != null) {
                    Text(
                        text = "$assignedGrade/$maxPoints",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (uiState.studentSubmission.late) {
                        Text(
                            text = stringResource(id = Strings.done_late),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                } else if (dueDate != null) {
                    if (dueDate.before(Date())) {
                        Text(
                            text = stringResource(id = Strings.missing),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else if (date != null) {
                        Text(
                            text = stringResource(id = Strings.due_, date),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Text(
                        text = stringResource(id = Strings.no_due_date),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            else -> {
                if (dueDate != null) {
                    if (dueDate.before(Date())) {
                        Text(
                            text = stringResource(id = Strings.missing),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else if (date != null) {
                        Text(
                            text = stringResource(id = Strings.due_, date),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Text(
                        text = stringResource(id = Strings.no_due_date),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}