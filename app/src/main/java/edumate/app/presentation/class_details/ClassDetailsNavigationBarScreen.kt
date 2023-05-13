package edumate.app.presentation.class_details

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.VideoCall
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
        Icons.Filled.QuestionAnswer,
        Icons.Outlined.QuestionAnswer
    )

    object Meet :
        ClassDetailsNavigationBarScreen(
            Screen.MeetScreen.route,
            Strings.label_meet_screen,
            Icons.Filled.VideoCall,
            Icons.Outlined.VideoCall
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