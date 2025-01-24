package app.edumate.presentation.teaching

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
import app.edumate.domain.model.courses.Courses
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.teaching.components.TeachingListItem

@Composable
fun TeachingScreen(
    teaching: List<Courses>,
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

    if (teaching.isEmpty()) {
        ErrorScreen(
            modifier = modifier.padding(bottom = bottomPadding),
            errorMessage = stringResource(id = R.string.add_a_class_to_get_started),
        )
    } else {
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
                        isOwner = true,
                        onShareClick = { /*TODO*/ },
                        onEditClick = { /*TODO*/ },
                        onDeleteClick = { /*TODO*/ },
                        onLeaveClick = { /*TODO*/ },
                    )
                }
            },
        )
    }
}
