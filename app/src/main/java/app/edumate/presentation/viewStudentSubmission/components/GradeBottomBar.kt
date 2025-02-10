package app.edumate.presentation.viewStudentSubmission.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.domain.model.studentSubmission.StudentSubmission
import app.edumate.domain.model.studentSubmission.SubmissionState
import app.edumate.presentation.viewStudentSubmission.toDraftGrade

@Composable
fun GradeBottomBar(
    studentSubmission: StudentSubmission,
    grade: TextFieldState,
    onReturnClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowInsets =
        WindowInsets.systemBars.union(WindowInsets.displayCutout).only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
        )
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val courseWork = studentSubmission.courseWork
    val maxPoints = courseWork?.maxPoints
    val isCourseWorkGraded = maxPoints != null && maxPoints > 0
    val draftGrade = grade.text.toString().toDraftGrade()
    val isReturnEnabled = shouldEnableReturn(studentSubmission, draftGrade, isCourseWorkGraded)

    Column(modifier = modifier) {
        HorizontalDivider(thickness = Dp.Hairline)
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(windowInsets)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isCourseWorkGraded) {
                TextField(
                    state = grade,
                    modifier = Modifier.weight(1f),
                    label = {
                        Text(text = stringResource(id = R.string.grade_, maxPoints))
                    },
                    suffix = {
                        Text(text = "/$maxPoints")
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                        ),
                    onKeyboardAction = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    },
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            TextButton(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onReturnClick()
                },
                enabled = isReturnEnabled,
            ) {
                Text(text = stringResource(id = R.string._return))
            }
        }
    }
}

private fun shouldEnableReturn(
    submission: StudentSubmission,
    draftGrade: Int?,
    isCourseWorkGraded: Boolean,
): Boolean =
    when {
        !isCourseWorkGraded -> submission.state == SubmissionState.TURNED_IN
        submission.assignedGrade != draftGrade -> true
        else -> false
    }
