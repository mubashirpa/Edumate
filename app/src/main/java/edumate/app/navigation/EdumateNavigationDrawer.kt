package edumate.app.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import edumate.app.R.string as Strings

@Composable
fun EdumateModalNavigationDrawer(
    navController: NavController,
    drawerState: DrawerState,
    content: @Composable () -> Unit,
) {
    val items = listOf(DrawerItem.Profile, DrawerItem.Settings)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .padding(horizontal = 28.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(id = Strings.app_name).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.close() }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuOpen,
                                contentDescription = null,
                            )
                        }
                    }
                }
                items.forEachIndexed { index, drawerItem ->
                    NavigationDrawerItem(
                        label = { Text(text = stringResource(id = drawerItem.labelId)) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            when (index) {
                                0 -> {
                                    navController.navigate(Screen.ProfileScreen.route)
                                }

                                1 -> {
                                    navController.navigate(Screen.SettingsScreen.route)
                                }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        icon = { Icon(imageVector = drawerItem.icon, contentDescription = null) },
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        },
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        content = content,
    )
}

private sealed class DrawerItem(
    @StringRes var labelId: Int,
    var icon: ImageVector,
) {
    data object Profile : DrawerItem(Strings.profile, Icons.Default.AccountCircle)

    data object Settings : DrawerItem(Strings.settings, Icons.Default.Settings)
}
