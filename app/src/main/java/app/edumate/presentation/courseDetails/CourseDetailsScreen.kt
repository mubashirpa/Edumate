package app.edumate.presentation.courseDetails

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import app.edumate.R
import app.edumate.core.Result
import app.edumate.domain.model.course.CourseWithMembers
import app.edumate.navigation.CourseDetailsNavHost
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.courseDetails.components.CourseDetailsNavigationBar

@Composable
fun CourseDetailsScreen(
    uiState: CourseDetailsUiState,
    onEvent: (CourseDetailsUiEvent) -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToCourseSettings: (id: String) -> Unit,
    onNavigateToImageViewer: (url: String, title: String?) -> Unit,
    onLeaveCourse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val courseResult = uiState.courseResult) {
        is Result.Empty -> {}

        is Result.Error -> {
            ErrorScreen(
                onRetryClick = {
                    onEvent(CourseDetailsUiEvent.Retry)
                },
                modifier = Modifier.fillMaxSize(),
                errorMessage = courseResult.message!!.asString(),
            )
        }

        is Result.Loading -> {
            LoadingScreen()
        }

        is Result.Success -> {
            val courseWithMembers = courseResult.data
            if (courseWithMembers != null) {
                CourseDetailsContent(
                    courseWithMembers = courseWithMembers,
                    currentUserRole = uiState.currentUserRole,
                    onNavigateUp = onNavigateUp,
                    onNavigateToCourseSettings = onNavigateToCourseSettings,
                    onNavigateToImageViewer = onNavigateToImageViewer,
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
    courseWithMembers: CourseWithMembers,
    currentUserRole: CourseUserRole,
    onNavigateUp: () -> Unit,
    onNavigateToCourseSettings: (id: String) -> Unit,
    onNavigateToImageViewer: (url: String, title: String?) -> Unit,
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
                courseId = courseWithMembers.id!!,
            )
        },
        contentWindowInsets =
            WindowInsets.systemBars
                .union(WindowInsets.displayCutout)
                .only(WindowInsetsSides.Bottom),
    ) { innerPadding ->
        CourseDetailsNavHost(
            navController = navController,
            courseWithMembers = courseWithMembers,
            currentUserRole = currentUserRole,
            onNavigateUp = onNavigateUp,
            onNavigateToCourseSettings = onNavigateToCourseSettings,
            onNavigateToImageViewer = onNavigateToImageViewer,
            onLeaveCourse = onLeaveCourse,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .imePadding(),
        )
    }
}
