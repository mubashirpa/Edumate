package edumate.app.presentation.class_details.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import edumate.app.R
import edumate.app.domain.model.courses.Course
import edumate.app.navigation.ClassDetailsNavHost
import edumate.app.presentation.class_details.screen.components.BottomNavigationBar
import edumate.app.presentation.components.EdumateSnackbarHost

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ClassDetailsScreen(
    // Here we are using another NavHost so we need a separate NavHostController
    classDetailsNavController: NavHostController = rememberNavController(),
    title: String?,
    courseId: String,
    onLeaveClass: () -> Unit,
    onBackPressed: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val course = Course(
        alternateLink = "https://edumate.web.app",
        id = courseId,
        ownerId = "XlPv3TJBFEbaZxWoOBHgFKuw4iy1",
        students = arrayListOf("NxyHm4f8Cedi9vRFf3l54WrHr1m2", "gy7Agvjr6SeNjB7UvAApQHesLgH2"),
        teachers = arrayListOf("XlPv3TJBFEbaZxWoOBHgFKuw4iy1", "KAGuI6VQI3NY78Ft42ej6a12cBm2")
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (title != null && title != "null") title else "")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!classDetailsNavController.navigateUp()) {
                            onBackPressed()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_up)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = classDetailsNavController)
        },
        snackbarHost = { EdumateSnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        ClassDetailsNavHost(
            navController = classDetailsNavController,
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding),
            course = course,
            snackbarHostState = snackbarHostState,
            onLeaveClass = onLeaveClass
        )
    }
}