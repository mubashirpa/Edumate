package edumate.app.presentation.classDetails

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.ui.graphics.vector.ImageVector
import edumate.app.navigation.Screen
import edumate.app.R.string as Strings

sealed class ClassDetailsNavigationBarScreen(
    var route: String,
    @StringRes val title: Int,
    var selectedIcon: ImageVector,
    var unselectedIcon: ImageVector,
) {
    data object Stream : ClassDetailsNavigationBarScreen(
        route = Screen.StreamScreen.route,
        title = Strings.label_stream_screen,
        selectedIcon = Icons.Filled.QuestionAnswer,
        unselectedIcon = Icons.Outlined.QuestionAnswer,
    )

    data object Classwork :
        ClassDetailsNavigationBarScreen(
            route = Screen.ClassworkScreen.route,
            title = Strings.label_classwork_screen,
            selectedIcon = Icons.AutoMirrored.Filled.Assignment,
            unselectedIcon = Icons.AutoMirrored.Outlined.Assignment,
        )

    data object People :
        ClassDetailsNavigationBarScreen(
            route = Screen.PeopleScreen.route,
            title = Strings.label_people_screen,
            selectedIcon = Icons.Filled.People,
            unselectedIcon = Icons.Outlined.People,
        )
}
