package app.edumate.presentation.settings

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import app.edumate.BuildConfig
import app.edumate.R
import app.edumate.domain.model.preferences.AppTheme
import app.edumate.presentation.settings.components.ListPreference
import app.edumate.presentation.settings.components.Preference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current
    val themes = stringArrayResource(id = R.array.app_theme).toList()
    val themeValues = AppTheme.entries.toList().map { it.name }
    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

    Column(modifier = modifier) {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.title_settings_screen))
            },
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                    )
                }
            },
            scrollBehavior = scrollBehavior,
        )
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            Preference(
                title = stringResource(id = R.string.notifications),
                summary = stringResource(id = R.string.manage_notification_settings),
                icon = Icons.Default.Notifications,
                onClick = {
                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                        }
                    context.startActivity(intent)
                },
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ListPreference(
                    title = stringResource(id = R.string.theme),
                    entries = themes,
                    entryValues = themeValues,
                    defaultValue = uiState.selectedTheme.name,
                    summary = themes[themeValues.indexOf(uiState.selectedTheme.name)],
                    icon = Icons.Default.BrightnessMedium,
                    onConfirmClick = { index, value ->
                        val appTheme = enumValueOf<AppTheme>(value)
                        onEvent(SettingsUiEvent.OnAppThemeChange(appTheme))

                        when (appTheme) {
                            AppTheme.SYSTEM_DEFAULT -> {
                                uiModeManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_AUTO)
                            }

                            AppTheme.LIGHT -> {
                                uiModeManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_NO)
                            }

                            AppTheme.DARK -> {
                                uiModeManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_YES)
                            }
                        }
                    },
                )
            }
        }
    }
}
