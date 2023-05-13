package edumate.app.core.ext

import android.content.res.Resources

val Int.dpToPx: Int
    get() {
        val density = Resources.getSystem().displayMetrics.density
        return (this * density + 0.5f).toInt()
    }