package edumate.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import edumate.app.core.utils.LocaleUtils
import edumate.app.presentation.settings.AppTheme
import edumate.app.presentation.ui.theme.EdumateTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val context = LocalContext.current

            LaunchedEffect(viewModel.uiState.appTheme) {
                when (viewModel.uiState.appTheme) {
                    AppTheme.SYSTEM_DEFAULT -> {
                        AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        )
                    }

                    AppTheme.LIGHT -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }

                    AppTheme.DARK -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
            }

            LaunchedEffect(viewModel.uiState.appLanguage) {
                LocaleUtils.updateResources(context, viewModel.uiState.appLanguage)
            }

            EdumateTheme(darkTheme = isAppInDarkTheme(appTheme = viewModel.uiState.appTheme)) {
                EdumateApp(viewModel.uiState.isLoggedIn)
            }
        }
    }

    @Composable
    private fun isAppInDarkTheme(appTheme: AppTheme) = when (appTheme) {
        AppTheme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }
}