package edumate.app.presentation.profile.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.presentation.components.UserAvatar
import edumate.app.presentation.profile.ProfileUiEvent
import edumate.app.presentation.profile.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onEvent: (ProfileUiEvent) -> Unit,
    onSignOut: () -> Unit,
    onBackPressed: () -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(id = Strings.title_profile_screen))
            },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = Strings.navigate_up)
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            UserAvatar(
                id = uiState.currentUser?.uid.orEmpty(),
                fullName = uiState.currentUser?.displayName ?: uiState.currentUser?.email.orEmpty(),
                photoUri = uiState.currentUser?.photoUrl,
                size = 114.dp,
                textStyle = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            ListItem(
                headlineContent = {
                    Text(text = uiState.currentUser?.displayName.orEmpty())
                },
                overlineContent = {
                    Text(text = stringResource(id = Strings.name))
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                }
            )
            Divider(modifier = Modifier.padding(start = 56.dp))
            ListItem(
                headlineContent = {
                    Text(text = uiState.currentUser?.email.orEmpty())
                },
                overlineContent = {
                    Text(text = stringResource(id = Strings.email))
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onEvent(ProfileUiEvent.SignOut)
                    onSignOut()
                }
            ) {
                Text(text = stringResource(id = Strings.logout))
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}