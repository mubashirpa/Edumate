package app.edumate.presentation.viewStudentSubmission.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.domain.model.studentSubmission.StudentSubmission
import app.edumate.domain.model.studentSubmission.SubmissionState

@Composable
fun GradeBottomBar(
    studentSubmission: StudentSubmission,
    grade: TextFieldState,
    onReturnClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val submissionState = studentSubmission.state
    val assignedGrade = studentSubmission.assignedGrade
    val courseWork = studentSubmission.courseWork!!
    val maxPoints = courseWork.maxPoints
    val isCourseWorkGraded = maxPoints != null && maxPoints > 0
    val draftGrade =
        grade.text
            .toString()
            .takeIf { it.isNotEmpty() }
            ?.toIntOrNull()
    val isReturnEnabled =
        when {
            !isCourseWorkGraded -> submissionState == SubmissionState.TURNED_IN
            submissionState == SubmissionState.TURNED_IN -> assignedGrade == null || draftGrade != assignedGrade
            else -> false
        }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isCourseWorkGraded) {
            OutlinedTextField(
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
        Button(
            onClick = onReturnClick,
            modifier = Modifier.padding(top = 8.dp),
            enabled = isReturnEnabled,
        ) {
            Text(text = stringResource(id = R.string._return))
        }
    }
}
