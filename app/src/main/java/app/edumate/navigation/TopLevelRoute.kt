package app.edumate.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.ui.graphics.vector.ImageVector
import app.edumate.R

data class TopLevelRoute<T : Screen>(
    @StringRes val labelId: Int,
    val route: T,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

fun courseDetailsLevelRoutes(courseId: String) =
    listOf(
        TopLevelRoute(
            labelId = R.string.label_stream_screen,
            route = Screen.Stream(courseId),
            selectedIcon = Icons.Filled.QuestionAnswer,
            unselectedIcon = Icons.Outlined.QuestionAnswer,
        ),
        TopLevelRoute(
            labelId = R.string.label_coursework_screen,
            route = Screen.Coursework(courseId),
            selectedIcon = Icons.AutoMirrored.Filled.Assignment,
            unselectedIcon = Icons.AutoMirrored.Outlined.Assignment,
        ),
        TopLevelRoute(
            labelId = R.string.label_people_screen,
            route = Screen.People(courseId),
            selectedIcon = Icons.Filled.People,
            unselectedIcon = Icons.Outlined.People,
        ),
    )
