package edumate.app.presentation.class_details.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import edumate.app.domain.model.courses.Course
import edumate.app.navigation.ClassDetailsNavHost
import edumate.app.presentation.class_details.screen.components.BottomNavigationBar
import edumate.app.presentation.components.EdumateSnackbarHost

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
        }
    ) { innerPadding ->
        ClassDetailsNavHost(
            navController = classDetailsNavController,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateLeftPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateRightPadding(LayoutDirection.Ltr),
                    bottom = innerPadding.calculateBottomPadding()
                ),
            course = course,
            snackbarHostState = snackbarHostState,
            onLeaveClass = onLeaveClass,
            onBackPressed = onBackPressed
        )
    }
}