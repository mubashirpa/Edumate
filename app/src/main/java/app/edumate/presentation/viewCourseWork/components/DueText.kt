package app.edumate.presentation.viewCourseWork.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import app.edumate.core.utils.DateTimeUtils
import app.edumate.core.utils.RelativeDate
import app.edumate.domain.model.courseWork.CourseWork
import app.edumate.domain.model.studentSubmission.StudentSubmission
import app.edumate.domain.model.studentSubmission.SubmissionState
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

@Composable
fun DueText(
    courseWork: CourseWork,
    studentSubmission: StudentSubmission,
    modifier: Modifier = Modifier,
) {
    val maxPoints = courseWork.maxPoints
    val assignedGrade = studentSubmission.assignedGrade
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
        if (maxPoints != null && maxPoints > 0 && assignedGrade != null) {
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
        } else {
            when {
                studentSubmission.state == SubmissionState.TURNED_IN -> {
                    Text(
                        text = stringResource(id = R.string.turned_in),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    late(isLate)
                }

                dueDateTime == null -> {
                    Text(
                        text = stringResource(id = R.string.no_due_date),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                DateTimeUtils.isPast(dueDateTime) -> {
                    Text(
                        text = stringResource(id = R.string.missing),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                else -> {
                    Text(
                        text = stringResource(id = R.string.assigned),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
fun CourseWorkDueText(
    dueTime: String,
    modifier: Modifier = Modifier,
) {
    val dueDateTime = Instant.parse(dueTime).toLocalDateTime(TimeZone.currentSystemDefault())
    val dueRelativeDate = DateTimeUtils.getRelativeDateStatus(dueDateTime.date)
    val formattedDueTime =
        dueDateTime.format(
            LocalDateTime.Format {
                time(
                    LocalTime.Format {
                        amPmHour()
                        char(':')
                        minute()
                        char(' ')
                        amPmMarker("AM", "PM")
                    },
                )
            },
        )
    val due =
        when (dueRelativeDate) {
            RelativeDate.TODAY -> {
                stringResource(id = R.string.due_today_, formattedDueTime)
            }

            RelativeDate.TOMORROW -> {
                stringResource(id = R.string.due_tomorrow_, formattedDueTime)
            }

            RelativeDate.YESTERDAY -> {
                stringResource(id = R.string.due_yesterday_, formattedDueTime)
            }

            RelativeDate.OTHER -> {
                val isThisYear = DateTimeUtils.isThisYear(dueDateTime.date)
                val formattedDueDate =
                    dueDateTime.format(
                        LocalDateTime.Format {
                            date(
                                LocalDate.Format {
                                    monthName(MonthNames.ENGLISH_ABBREVIATED)
                                    char(' ')
                                    dayOfMonth()
                                    chars(", ")
                                    if (!isThisYear) {
                                        year()
                                    }
                                },
                            )
                            if (isThisYear) {
                                time(
                                    LocalTime.Format {
                                        amPmHour()
                                        char(':')
                                        minute()
                                        char(' ')
                                        amPmMarker("AM", "PM")
                                    },
                                )
                            }
                        },
                    )

                stringResource(id = R.string.due_, formattedDueDate)
            }
        }

    Text(
        text = due,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium,
    )
}
