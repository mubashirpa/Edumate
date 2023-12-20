package edumate.app.presentation.home

import androidx.annotation.StringRes
import edumate.app.R.string as Strings

sealed class HomeTabsScreen(@StringRes val title: Int) {
    data object Enrolled : HomeTabsScreen(Strings.enrolled)
    data object Teaching : HomeTabsScreen(Strings.teaching)
}