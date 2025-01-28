package app.edumate.presentation.courseDetails

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import app.edumate.R
import app.edumate.core.Result
import app.edumate.domain.model.courses.Course
import app.edumate.navigation.CourseDetailsNavHost
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.courseDetails.components.CourseDetailsNavigationBar

@Composable
fun CourseDetailsScreen(
    uiState: CourseDetailsUiState,
    onEvent: (CourseDetailsUiEvent) -> Unit,
    onNavigateUp: () -> Unit,
    onLeaveCourse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val courseResult = uiState.courseResult) {
        is Result.Empty -> {}

        is Result.Error -> {
            ErrorScreen(
                onRetryClick = {
                    onEvent(CourseDetailsUiEvent.OnRetry)
                },
                modifier = Modifier.fillMaxSize(),
                errorMessage = courseResult.message!!.asString(),
            )
        }

        is Result.Loading -> {
            LoadingScreen()
        }

        is Result.Success -> {
            val course = courseResult.data
            if (course != null) {
                CourseDetailsContent(
                    course = course,
                    onNavigateUp = onNavigateUp,
                    onLeaveCourse = onLeaveCourse,
                    modifier = modifier,
                )
            } else {
                ErrorScreen(
                    modifier = Modifier.fillMaxSize(),
                    errorMessage = stringResource(R.string.class_not_found),
                )
            }
        }
    }
}

@Composable
fun CourseDetailsContent(
    course: Course,
    onNavigateUp: () -> Unit,
    onLeaveCourse: () -> Unit,
    modifier: Modifier = Modifier,
    // Separate NavHostController for nested navigation
    navController: NavHostController = rememberNavController(),
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            CourseDetailsNavigationBar(
                navController = navController,
                courseId = course.id.orEmpty(),
            )
        },
    ) { innerPadding ->
        CourseDetailsNavHost(
            navController = navController,
            course = course,
            onNavigateUp = onNavigateUp,
            onLeaveCourse = onLeaveCourse,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
        )
    }
}
