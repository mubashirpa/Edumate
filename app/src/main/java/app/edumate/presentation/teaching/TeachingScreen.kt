package app.edumate.presentation.teaching

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.utils.IntentUtils
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LeaveCourseDialog
import app.edumate.presentation.home.HomeUiEvent
import app.edumate.presentation.home.HomeUiState
import app.edumate.presentation.teaching.components.DeleteCourseDialog
import app.edumate.presentation.teaching.components.TeachingListItem

@Composable
fun TeachingScreen(
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    innerPadding: PaddingValues,
    onNavigateToCreateCourse: (courseId: String?) -> Unit,
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
    val teaching = uiState.teachingCourses

    if (teaching.isEmpty()) {
        ErrorScreen(
            modifier = modifier.padding(bottom = bottomPadding),
            errorMessage = stringResource(id = R.string.add_a_class_to_get_started),
        )
    } else {
        val context = LocalContext.current
        LazyColumn(
            modifier = modifier,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = {
                items(
                    items = teaching,
                    key = { it.course!!.id!! },
                ) { courses ->
                    TeachingListItem(
                        onClick = { id ->
                            onNavigateToClassDetails(id)
                        },
                        teachingCourse = courses.course!!,
                        isOwner = courses.course.ownerId == uiState.currentUser?.id,
                        onShareClick = { url ->
                            IntentUtils.shareText(context, url)
                        },
                        onEditClick = {
                            onNavigateToCreateCourse(courses.course.id)
                        },
                        onDeleteClick = {
                            onEvent(HomeUiEvent.OnOpenDeleteCourseDialogChange(courses.course.id))
                        },
                        onLeaveClick = {
                            onEvent(HomeUiEvent.OnOpenLeaveCourseDialogChange(courses.course))
                        },
                    )
                }
            },
        )
    }

    DeleteCourseDialog(
        onDismissRequest = {
            onEvent(HomeUiEvent.OnOpenDeleteCourseDialogChange(null))
        },
        open = uiState.deleteCourseId != null,
        onConfirmButtonClick = {
            onEvent(HomeUiEvent.DeleteCourse(uiState.deleteCourseId!!))
        },
    )

    LeaveCourseDialog(
        onDismissRequest = {
            onEvent(HomeUiEvent.OnOpenLeaveCourseDialogChange(null))
        },
        open = uiState.leaveCourse != null,
        name = uiState.leaveCourse?.name,
        onConfirmButtonClick = {
            uiState.leaveCourse?.id?.let { id ->
                onEvent(HomeUiEvent.LeaveCourse(id))
            }
        },
    )
}
