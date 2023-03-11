package edumate.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import edumate.app.presentation.classwork.screen.ClassworkScreen
import edumate.app.presentation.people.screen.PeopleScreen
import edumate.app.presentation.stream.screen.StreamScreen

@Composable
fun ClassDetailsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    courseId: String
) {
    NavHost(
        navController = navController,
        startDestination = Screen.StreamScreen.route,
        modifier = modifier
    ) {
        val arguments: List<NamedNavArgument> = listOf(
            navArgument(Routes.Args.CLASS_DETAILS_COURSE_ID) {
                type = NavType.StringType
                defaultValue = courseId
            }
        )

        composable(
            route = Screen.StreamScreen.route,
            arguments = arguments
        ) {
            StreamScreen()
        }
        composable(
            route = Screen.ClassworkScreen.route,
            arguments = arguments
        ) {
            ClassworkScreen()
        }
        composable(
            route = Screen.PeopleScreen.route,
            arguments = arguments
        ) {
            PeopleScreen()
        }
    }
}