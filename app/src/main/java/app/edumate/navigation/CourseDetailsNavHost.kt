package app.edumate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.edumate.domain.model.courses.Course
import app.edumate.presentation.components.EmptyComingSoon
import app.edumate.presentation.courseWork.CourseWorkScreen
import app.edumate.presentation.courseWork.CourseWorkViewModel
import app.edumate.presentation.people.PeopleScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun CourseDetailsNavHost(
    navController: NavHostController,
    course: Course,
    onNavigateUp: () -> Unit,
    onLeaveCourse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Stream(course.id.orEmpty()),
        modifier = modifier,
    ) {
        composable<Screen.Stream> {
            EmptyComingSoon()
        }
        composable<Screen.CourseWork> {
            val viewModel: CourseWorkViewModel = koinViewModel()
            CourseWorkScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                course = course,
                onNavigateUp = onNavigateUp,
                onNavigateToCreateClasswork = { _, _ ->
                    // TODO: Create classwork
                },
                onNavigateToViewClasswork = { _, _ ->
                    // TODO: View classwork
                },
            )
        }
        composable<Screen.People> {
            PeopleScreen(
                course = course,
                onNavigateUp = onNavigateUp,
                onLeaveCourseComplete = onLeaveCourse,
            )
        }
    }
}
