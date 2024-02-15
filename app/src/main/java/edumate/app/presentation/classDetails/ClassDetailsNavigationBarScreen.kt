package edumate.app.presentation.classDetails

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.VideoCall
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
        Screen.StreamScreen.route,
        Strings.label_stream_screen,
        Icons.Filled.QuestionAnswer,
        Icons.Outlined.QuestionAnswer,
    )

    data object Meet :
        ClassDetailsNavigationBarScreen(
            Screen.MeetScreen.route,
            Strings.label_meet_screen,
            Icons.Filled.VideoCall,
            Icons.Outlined.VideoCall,
        )

    data object Classwork :
        ClassDetailsNavigationBarScreen(
            Screen.ClassworkScreen.route,
            Strings.label_classwork_screen,
            Icons.AutoMirrored.Filled.Assignment,
            Icons.AutoMirrored.Outlined.Assignment,
        )

    data object People :
        ClassDetailsNavigationBarScreen(
            Screen.PeopleScreen.route,
            Strings.label_people_screen,
            Icons.Filled.People,
            Icons.Outlined.People,
        )
}
