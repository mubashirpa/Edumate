package edumate.app.presentation.home

import androidx.annotation.StringRes
import edumate.app.R.string as Strings

sealed class HomeTabsScreen(@StringRes val title: Int) {
    object Enrolled : HomeTabsScreen(Strings.enrolled)
    object Teaching : HomeTabsScreen(Strings.teaching)
}