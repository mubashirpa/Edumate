package app.edumate.presentation.enrolled

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.enrolled.components.EnrolledListItem
import app.edumate.presentation.enrolled.components.UnEnrollDialog
import app.edumate.presentation.home.HomeUiEvent
import app.edumate.presentation.home.HomeUiState

@Composable
fun EnrolledScreen(
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    innerPadding: PaddingValues,
    onNavigateToClassDetails: (courseId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomPadding = innerPadding.calculateBottomPadding()
    val contentPadding =
        PaddingValues(
            start = 16.dp,
            top = 12.dp,
            end = 16.dp,
            bottom = bottomPadding + 100.dp,
        )
    val enrolled = uiState.enrolledCourses

    if (enrolled.isEmpty()) {
        ErrorScreen(
            modifier = modifier.padding(bottom = bottomPadding),
            errorMessage = stringResource(id = R.string.join_a_class_to_get_started),
        )
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = {
                items(
                    items = enrolled,
                    key = { it.id!! },
                ) { course ->
                    EnrolledListItem(
                        onClick = { id ->
                            onNavigateToClassDetails(id)
                        },
                        enrolledCourse = course,
                        onCourseUnenroll = { id ->
                            onEvent(HomeUiEvent.OnOpenUnenrollDialogChange(id))
                        },
                    )
                }
            },
        )
    }

    UnEnrollDialog(
        onDismissRequest = {
            onEvent(HomeUiEvent.OnOpenUnenrollDialogChange(null))
        },
        open = uiState.unenrollCourseId != null,
        onConfirmButtonClick = {
            onEvent(HomeUiEvent.UnenrollCourse(uiState.unenrollCourseId!!))
        },
    )
}
