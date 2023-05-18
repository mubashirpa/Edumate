package edumate.app.core.utils

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import java.util.Locale

object LocaleUtils {

    // private val primaryLocale: Locale = context.resources.configuration.locales[0]
    // val locale: String = primaryLocale.displayName

    fun updateConfiguration(context: Context, language: String) {
        val locale = Locale(language)
        val configuration = Configuration().apply {
            setLocale(locale)
        }
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }

    fun updateResources(context: Context, language: String): Context {
        val resources = context.resources
        val locale = Locale(language)
        val configuration = Configuration(resources.configuration).apply {
            setLocale(locale)
        }
        return context.createConfigurationContext(configuration)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun setApplicationLocales(context: Context, language: String) {
        context.getSystemService(LocaleManager::class.java).applicationLocales =
            LocaleList(Locale.forLanguageTag(language))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun currentAppLocales(context: Context): LocaleList {
        return context.getSystemService(LocaleManager::class.java).applicationLocales
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun resetToSystemLocale(context: Context) {
        context.getSystemService(LocaleManager::class.java).applicationLocales =
            LocaleList.getEmptyLocaleList()
    }
}