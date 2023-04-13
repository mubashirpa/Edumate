package edumate.app.presentation.view_classwork

import androidx.annotation.StringRes
import edumate.app.R.string as Strings

sealed class ViewClassworkTabsScreen(@StringRes val title: Int) {
    object Instructions : ViewClassworkTabsScreen(Strings.instructions)
    object StudentAnswers : ViewClassworkTabsScreen(Strings.student_answers)
    object StudentWork : ViewClassworkTabsScreen(Strings.student_work)
    object Question : ViewClassworkTabsScreen(Strings.question)
}