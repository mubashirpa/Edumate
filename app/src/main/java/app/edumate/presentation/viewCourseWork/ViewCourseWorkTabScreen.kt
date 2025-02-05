package app.edumate.presentation.viewCourseWork

import androidx.annotation.StringRes
import app.edumate.R

sealed class ViewCourseWorkTabScreen(
    @StringRes val title: Int,
) {
    data object Instructions : ViewCourseWorkTabScreen(R.string.instructions)

    data object StudentAnswers : ViewCourseWorkTabScreen(R.string.label_student_answers)

    data object StudentWork : ViewCourseWorkTabScreen(R.string.label_student_work)

    data object Question : ViewCourseWorkTabScreen(R.string.question)
}
