package edumate.app.presentation.class_details.screen.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import edumate.app.presentation.class_details.ClassDetailsNavigationBarScreen

@Composable
fun BottomNavigationBar(navController: NavController) {
    val screens = listOf(
        ClassDetailsNavigationBarScreen.Stream,
        ClassDetailsNavigationBarScreen.Classwork,
        ClassDetailsNavigationBarScreen.People
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomBarDestination = screens.any { it.route == currentDestination?.route }

    if (bottomBarDestination) {
        NavigationBar {
            screens.forEach { screen ->
                val selected =
                    currentDestination?.hierarchy?.any { it.route == screen.route } == true

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(currentDestination?.route!!) {
                                inclusive = true
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        val icon = if (selected) {
                            screen.selectedIcon
                        } else {
                            screen.unselectedIcon
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = stringResource(id = screen.title)
                        )
                    },
                    label = { Text(stringResource(id = screen.title)) }
                )
            }
        }
    }
}