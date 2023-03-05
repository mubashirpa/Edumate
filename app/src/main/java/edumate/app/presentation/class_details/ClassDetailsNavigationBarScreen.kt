package edumate.app.presentation.class_details

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import edumate.app.R.string as Strings
import edumate.app.navigation.Screen

sealed class ClassDetailsNavigationBarScreen(
    var route: String,
    @StringRes val title: Int,
    var selectedIcon: ImageVector,
    var unselectedIcon: ImageVector
) {
    object Stream : ClassDetailsNavigationBarScreen(
        Screen.StreamScreen.route,
        Strings.label_stream_screen,
        Icons.Filled.Forum,
        Icons.Outlined.Forum
    )

    object Classwork :
        ClassDetailsNavigationBarScreen(
            Screen.ClassworkScreen.route,
            Strings.label_classwork_screen,
            Icons.Filled.Assignment,
            Icons.Outlined.Assignment
        )

    object People :
        ClassDetailsNavigationBarScreen(
            Screen.PeopleScreen.route,
            Strings.label_people_screen,
            Icons.Filled.People,
            Icons.Outlined.People
        )
}