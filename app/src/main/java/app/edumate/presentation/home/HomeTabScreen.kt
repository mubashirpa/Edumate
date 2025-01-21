package app.edumate.presentation.home

import androidx.annotation.StringRes
import app.edumate.R

sealed class HomeTabScreen(
    @StringRes val title: Int,
) {
    data object Enrolled : HomeTabScreen(R.string.enrolled)

    data object Teaching : HomeTabScreen(R.string.teaching)
}
