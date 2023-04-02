package edumate.app.presentation.class_details.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import edumate.app.domain.model.courses.Course
import edumate.app.navigation.ClassDetailsNavHost
import edumate.app.presentation.class_details.screen.components.BottomNavigationBar
import edumate.app.presentation.components.EdumateSnackbarHost

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClassDetailsScreen(
    // Here we are using another NavHost so we need a separate NavHostController
    classDetailsNavController: NavHostController = rememberNavController(),
    courseId: String,
    onLeaveClass: () -> Unit,
    onBackPressed: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val course = Course(
        alternateLink = "https://edumate.web.app",
        id = courseId,
        name = "Compiler Design",
        ownerId = "XlPv3TJBFEbaZxWoOBHgFKuw4iy1",
        students = arrayListOf("NxyHm4f8Cedi9vRFf3l54WrHr1m2", "gy7Agvjr6SeNjB7UvAApQHesLgH2"),
        teachers = arrayListOf("XlPv3TJBFEbaZxWoOBHgFKuw4iy1", "KAGuI6VQI3NY78Ft42ej6a12cBm2")
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = classDetailsNavController)
        },
        snackbarHost = {
            EdumateSnackbarHost(snackbarHostState)
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
                )
        ) {
            ClassDetailsNavHost(
                navController = classDetailsNavController,
                modifier = Modifier.fillMaxSize(),
                course = course,
                snackbarHostState = snackbarHostState,
                onLeaveClass = onLeaveClass,
                onBackPressed = onBackPressed
            )
        }
    }
}