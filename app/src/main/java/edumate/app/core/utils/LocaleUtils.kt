package edumate.app.core.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleUtils {

    // [AppPrefs] is sharedPreferences or datastore
    // fun setLocale(c: Context, pref: SharedPreferences) = updateResources(c, pref.language ?: "en")

    fun updateResources(context: Context, language: String) {
        // TODO("Fix deprecations")
        val locale = Locale(language)
        val configuration = Configuration().apply {
            setLocale(locale)
        }
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }

    fun updateResources2(context: Context, language: String) {
        val locale = Locale(language)
        val configuration = Configuration(context.resources.configuration).apply {
            setLocale(locale)
        }
        val contextWithLocale = context.createConfigurationContext(configuration)
    }
}