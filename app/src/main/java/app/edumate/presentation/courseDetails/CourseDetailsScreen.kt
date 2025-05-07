package app.edumate.presentation.courseDetails

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import app.edumate.core.Result
import app.edumate.domain.model.course.CourseWithMembers
import app.edumate.navigation.CourseDetailsNavHost
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.courseDetails.components.CourseDetailsNavigationBar
import com.google.android.play.core.review.ReviewManager
import org.koin.compose.koinInject

@Composable
fun CourseDetailsScreen(
    uiState: CourseDetailsUiState,
    onEvent: (CourseDetailsUiEvent) -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToCourseSettings: (id: String) -> Unit,
    onNavigateToImageViewer: (url: String, title: String?) -> Unit,
    onNavigateToPdfViewer: (url: String, title: String?) -> Unit,
    onLeaveCourse: () -> Unit,
    modifier: Modifier = Modifier,
    reviewManager: ReviewManager = koinInject(),
) {
    val activity = LocalActivity.current as ComponentActivity

    BackHandler(uiState.openReviewDialog) {
        onEvent(CourseDetailsUiEvent.ReviewDialogShown)
        showReview(reviewManager, activity) {
            onNavigateUp()
        }
    }

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
            val courseWithMembers = courseResult.data!!
            CourseDetailsContent(
                courseWithMembers = courseWithMembers,
                currentUserRole = uiState.currentUserRole,
                onNavigateUp = onNavigateUp,
                onNavigateToCourseSettings = onNavigateToCourseSettings,
                onNavigateToImageViewer = onNavigateToImageViewer,
                onNavigateToPdfViewer = onNavigateToPdfViewer,
                onLeaveCourse = onLeaveCourse,
                modifier = modifier,
            )
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
    onNavigateToPdfViewer: (url: String, title: String?) -> Unit,
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
            onNavigateToPdfViewer = onNavigateToPdfViewer,
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

private fun showReview(
    reviewManager: ReviewManager,
    activity: ComponentActivity,
    onReviewComplete: () -> Unit,
) {
    val request = reviewManager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val reviewInfo = task.result
            val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
            flow.addOnCompleteListener { _ ->
                onReviewComplete()
            }
        } else {
            onReviewComplete()
        }
    }
}
