package edumate.app.presentation.view_classwork

import androidx.annotation.StringRes
import edumate.app.R.string as Strings

sealed class ViewClassworkTabsScreen(
    @StringRes val title: Int,
) {
    data object Instructions : ViewClassworkTabsScreen(Strings.instructions)

    data object StudentAnswers : ViewClassworkTabsScreen(Strings.student_answers)

    data object StudentWork : ViewClassworkTabsScreen(Strings.student_work)

    data object Question : ViewClassworkTabsScreen(Strings.question)
}
