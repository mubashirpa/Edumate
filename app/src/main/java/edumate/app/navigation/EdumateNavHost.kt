package edumate.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import edumate.app.presentation.class_details.screen.ClassDetailsScreen
import edumate.app.presentation.create_class.screen.CreateClassScreen
import edumate.app.presentation.home.screen.HomeScreen
import edumate.app.presentation.join_class.screen.JoinClassScreen
import edumate.app.presentation.profile.screen.ProfileScreen

@Composable
fun EdumateNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Routes.Graph.AUTHENTICATION
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        authentication(navController)
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(
                navigateToClassDetails = { title, courseId ->
                    navController.navigate(Screen.ClassDetailsScreen.withArgs(title, courseId))
                },
                navigateToCreateClass = { courseId ->
                    val createClassRoute = if (courseId != null) {
                        "${Screen.CreateClassScreen.route}?${Routes.Args.CREATE_CLASS_COURSE_ID}=$courseId"
                    } else {
                        Screen.CreateClassScreen.route
                    }
                    navController.navigate(createClassRoute)
                },
                navigateToJoinClass = {
                    navController.navigate(Screen.JoinClassScreen.route)
                }
            )
        }
        composable(
            route = "${Screen.CreateClassScreen.route}${Routes.Args.CREATE_CLASS_SCREEN}",
            arguments = listOf(
                navArgument(Routes.Args.CREATE_CLASS_COURSE_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val courseId =
                backStackEntry.arguments?.getString(Routes.Args.CREATE_CLASS_COURSE_ID)

            CreateClassScreen(
                courseId = courseId,
                navigateToClassDetails = {
                    navController.navigate(Screen.ClassDetailsScreen.withArgs(null, it)) {
                        popUpTo(Screen.HomeScreen.route)
                        launchSingleTop = true
                    }
                },
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
        composable(route = Screen.JoinClassScreen.route) {
            JoinClassScreen(
                navigateToClassDetails = { courseId ->
                    navController.navigate(Screen.ClassDetailsScreen.withArgs(null, courseId)) {
                        popUpTo(Screen.HomeScreen.route)
                        launchSingleTop = true
                    }
                },
                navigateToProfile = {
                    navController.navigate(Screen.ProfileScreen.route)
                },
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
        composable(
            route = "${Screen.ClassDetailsScreen.route}${Routes.Args.CLASS_DETAILS_SCREEN}",
            arguments = listOf(
                navArgument(Routes.Args.CLASS_DETAILS_TITLE) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(Routes.Args.CLASS_DETAILS_COURSE_ID) {
                    type = NavType.StringType
                    defaultValue = Routes.Args.CLASS_DETAILS_DEFAULT_COURSE_ID
                }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString(Routes.Args.CLASS_DETAILS_TITLE)
            val courseId =
                backStackEntry.arguments?.getString(Routes.Args.CLASS_DETAILS_COURSE_ID).orEmpty()
            ClassDetailsScreen(
                title = title,
                courseId = courseId,
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
        composable(route = Screen.ProfileScreen.route) {
            ProfileScreen()
        }
    }
}