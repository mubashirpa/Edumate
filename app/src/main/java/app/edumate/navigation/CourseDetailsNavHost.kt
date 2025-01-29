package app.edumate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import app.edumate.domain.model.course.CourseWithMembers
import app.edumate.presentation.components.EmptyComingSoon
import app.edumate.presentation.courseDetails.CurrentUserRole
import app.edumate.presentation.courseWork.CourseWorkScreen
import app.edumate.presentation.courseWork.CourseWorkViewModel
import app.edumate.presentation.createCourseWork.CreateCourseWorkScreen
import app.edumate.presentation.people.PeopleScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun CourseDetailsNavHost(
    navController: NavHostController,
    courseWithMembers: CourseWithMembers,
    currentUserRole: CurrentUserRole,
    onNavigateUp: () -> Unit,
    onLeaveCourse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Stream(courseWithMembers.id!!),
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
                courseWithMembers = courseWithMembers,
                currentUserRole = currentUserRole,
                onNavigateUp = onNavigateUp,
                onNavigateToCreateClasswork = { workType, id ->
                    navController.navigate(Screen.CreateCourseWork(workType, id))
                },
                onNavigateToViewClasswork = {
                    // TODO: View classwork
                },
            )
        }
        composable<Screen.People> {
            PeopleScreen(
                courseWithMembers = courseWithMembers,
                currentUserRole = currentUserRole,
                onNavigateUp = onNavigateUp,
                onLeaveCourseComplete = onLeaveCourse,
            )
        }
        composable<Screen.CreateCourseWork> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.CreateCourseWork>()
            CreateCourseWorkScreen(
                courseName = courseWithMembers.name.orEmpty(),
                workType = route.workType,
                onNavigateUp = navController::navigateUp,
                onCreateCourseWorkComplete = {
                    // TODO
                    navController.navigateUp()
                },
                courseWorkId = route.id,
            )
        }
    }
}
