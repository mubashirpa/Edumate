package app.edumate.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.edumate.domain.model.courses.Course
import app.edumate.presentation.components.EmptyComingSoon
import app.edumate.presentation.people.PeopleScreen

@Composable
fun CourseDetailsNavHost(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
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
        composable<Screen.Coursework> {
            EmptyComingSoon()
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
