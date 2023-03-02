package edumate.app.presentation.home.screen.components

import androidx.annotation.StringRes
import edumate.app.R.string as Strings

sealed class TabScreen(@StringRes val title: Int) {
    object Enrolled : TabScreen(Strings.enrolled)
    object Teaching : TabScreen(Strings.teaching)
}