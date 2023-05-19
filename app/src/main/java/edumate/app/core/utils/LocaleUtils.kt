package edumate.app.core.utils

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import java.util.Locale

object LocaleUtils {

    fun getLocale(context: Context): String {
        val primaryLocale: Locale = context.resources.configuration.locales[0]
        return primaryLocale.language
    }

    @Suppress("DEPRECATION")
    fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        val resources = context.resources
        val configuration = Configuration(resources.configuration).apply {
            setLocale(locale)
        }
        val configurationContext = context.createConfigurationContext(configuration)
        // TODO("Remove deprecated method")
        resources.updateConfiguration(configuration, context.resources.displayMetrics)
        return configurationContext
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