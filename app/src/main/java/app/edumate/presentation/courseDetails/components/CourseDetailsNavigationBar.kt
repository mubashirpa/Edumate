package app.edumate.presentation.courseDetails.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.edumate.navigation.courseDetailsLevelRoutes
import app.edumate.presentation.theme.EdumateTheme

@Composable
fun CourseDetailsNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        courseDetailsLevelRoutes.forEach { topLevelRoute ->
            val selected =
                currentDestination?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(topLevelRoute.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) topLevelRoute.selectedIcon else topLevelRoute.unselectedIcon,
                        contentDescription = stringResource(id = topLevelRoute.labelId),
                    )
                },
                label = {
                    Text(text = stringResource(id = topLevelRoute.labelId))
                },
            )
        }
    }
}

@Preview
@Composable
private fun CourseDetailsNavigationBarPreview() {
    EdumateTheme {
        CourseDetailsNavigationBar(navController = rememberNavController())
    }
}
