package edumate.app.presentation.settings.screen

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edumate.app.core.ext.dpToPx
import edumate.app.core.utils.LocaleUtils
import edumate.app.core.utils.enumValueOf
import edumate.app.presentation.settings.AppTheme
import edumate.app.presentation.settings.SettingsUiEvent
import edumate.app.presentation.settings.SettingsUiState
import kotlinx.coroutines.launch
import edumate.app.R.array as Arrays
import edumate.app.R.string as Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit,
    onBackPressed: () -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val context = LocalContext.current
    val themes = stringArrayResource(id = Arrays.app_theme).toList()
    val themeValues = listOf(AppTheme.SYSTEM_DEFAULT.name, AppTheme.LIGHT.name, AppTheme.DARK.name)
    val languages = stringArrayResource(id = Arrays.app_language).toList()
    val languageValues = stringArrayResource(id = Arrays.app_language_values).toList()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = stringResource(id = Strings.title_settings_screen)) },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = Strings.navigate_up)
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            ListPreference(
                title = stringResource(id = Strings.theme),
                entries = themes,
                entryValues = themeValues,
                defaultValue = uiState.selectedTheme.name,
                summary = themes[themeValues.indexOf(uiState.selectedTheme.name)],
                icon = Icons.Default.BrightnessMedium,
                onConfirmClick = { index, value ->
                    val appTheme = value.enumValueOf(AppTheme.SYSTEM_DEFAULT)!!
                    onEvent(SettingsUiEvent.OnAppThemeChange(index, appTheme))
                }
            )
            Preference(
                title = stringResource(id = Strings.notifications),
                summary = stringResource(id = Strings.manage_notification_settings),
                icon = Icons.Default.Notifications,
                onClick = {
                    val settingsIntent = Intent().apply {
                        action = "android.settings.APP_NOTIFICATION_SETTINGS"
                        putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
                    }
                    context.startActivity(settingsIntent)
                }
            )
            ListPreference(
                title = stringResource(id = Strings.app_language),
                entries = languages,
                entryValues = languageValues,
                defaultValue = uiState.selectedLanguage,
                summary = languages[languageValues.indexOf(uiState.selectedLanguage)],
                icon = Icons.Default.Language,
                onConfirmClick = { index, value ->
                    onEvent(SettingsUiEvent.OnAppLanguageChange(index, value))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        LocaleUtils.setApplicationLocales(context, value)
                    }
                }
            )
        }
    }
}

@Composable
private fun Preference(
    title: String,
    summary: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(text = title)
        },
        modifier = Modifier.clickable(onClick = onClick),
        supportingContent = if (summary != null) {
            {
                Text(text = summary)
            }
        } else {
            null
        },
        leadingContent = if (icon != null) {
            {
                Icon(imageVector = icon, contentDescription = null)
            }
        } else {
            null
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListPreference(
    title: String,
    entries: List<String>,
    entryValues: List<String> = emptyList(),
    defaultValue: String? = null,
    summary: String? = null,
    icon: ImageVector? = null,
    onConfirmClick: (Int, String) -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val openDialog = remember { mutableStateOf(false) }
    val defaultSelectedOption = if (entryValues.isNotEmpty()) {
        entryValues.indexOf(defaultValue)
    } else {
        entries.indexOf(defaultValue)
    }
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(0) }
    val itemHeightPx = remember { 56.dpToPx }

    ListItem(
        headlineContent = {
            Text(text = title)
        },
        modifier = Modifier.clickable {
            if (defaultSelectedOption >= 0) {
                onOptionSelected(defaultSelectedOption)
                if (scrollState.maxValue > 0) {
                    coroutineScope.launch {
                        scrollState.scrollTo((itemHeightPx * defaultSelectedOption))
                    }
                }
            }
            openDialog.value = true
        },
        supportingContent = if (summary != null) {
            {
                Text(text = summary)
            }
        } else {
            null
        },
        leadingContent = if (icon != null) {
            {
                Icon(imageVector = icon, contentDescription = null)
            }
        } else {
            null
        }
    )

    if (openDialog.value && entries.isNotEmpty()) {
        BasicAlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = AlertDialogDefaults.shape,
                color = AlertDialogDefaults.containerColor,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(vertical = 24.dp)
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (scrollState.canScrollBackward) {
                        HorizontalDivider()
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .selectableGroup()
                            .verticalScroll(scrollState)
                    ) {
                        entries.forEachIndexed { index, text ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .selectable(
                                        selected = (index == selectedOption),
                                        onClick = {
                                            onOptionSelected(index)
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 24.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (index == selectedOption),
                                    onClick = null // null recommended for accessibility with screen readers
                                )
                                Text(
                                    text = text,
                                    modifier = Modifier.padding(start = 16.dp),
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                    if (scrollState.canScrollForward) {
                        HorizontalDivider()
                    }
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(top = 16.dp)
                            .align(Alignment.End)
                    ) {
                        TextButton(onClick = { openDialog.value = false }) {
                            Text(stringResource(id = Strings.cancel))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                openDialog.value = false
                                if (entryValues.isNotEmpty()) {
                                    onConfirmClick(
                                        selectedOption,
                                        entryValues[selectedOption]
                                    )
                                } else {
                                    onConfirmClick(
                                        selectedOption,
                                        entries[selectedOption]
                                    )
                                }
                            }
                        ) {
                            Text(stringResource(id = Strings.ok))
                        }
                    }
                }
            }
        }
    }
}