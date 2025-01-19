package app.edumate.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import app.edumate.R
import app.edumate.core.Result
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.components.ProgressDialog
import app.edumate.presentation.components.UserAvatar
import app.edumate.presentation.theme.EdumateTheme
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    onNavigateUp: () -> Unit,
    onSignOutComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val currentOnSignOutComplete by rememberUpdatedState(onSignOutComplete)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { viewModel.uiState }
            .filter { it.isUserSignOut }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnSignOutComplete()
            }
    }

    ProfileContent(
        uiState = viewModel.uiState,
        onEvent = viewModel::onEvent,
        onNavigateUp = onNavigateUp,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onEvent: (ProfileUiEvent) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.title_profile_screen))
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
        when (val result = uiState.currentUserResult) {
            is Result.Empty -> {}

            is Result.Error -> {
                ErrorScreen(
                    modifier = Modifier.fillMaxSize(),
                    errorMessage = result.message!!.asString(),
                )
            }

            is Result.Loading -> {
                LoadingScreen()
            }

            is Result.Success -> {
                val currentUser = result.data
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(top = 16.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    UserAvatar(
                        id = currentUser?.id.orEmpty(),
                        fullName =
                            currentUser?.displayName
                                ?: currentUser?.emailAddress.orEmpty(),
                        photoUrl = currentUser?.photoUrl,
                        size = 96.dp,
                        shape = MaterialTheme.shapes.extraLarge,
                        textStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 36.sp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ListItem(
                        headlineContent = {
                            Text(text = currentUser?.displayName.orEmpty())
                        },
                        overlineContent = {
                            Text(text = stringResource(R.string.name))
                        },
                        leadingContent = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null)
                        },
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                    ListItem(
                        headlineContent = {
                            Text(text = currentUser?.emailAddress.orEmpty())
                        },
                        overlineContent = {
                            Text(text = stringResource(id = R.string.email))
                        },
                        leadingContent = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null)
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            onEvent(ProfileUiEvent.SignOut)
                        },
                    ) {
                        Text(text = stringResource(R.string.logout))
                    }
                }
            }
        }
    }

    ProgressDialog(
        text = stringResource(R.string.logging_out),
        openDialog = uiState.openProgressDialog,
        onDismissRequest = {},
    )
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    EdumateTheme {
        ProfileContent(
            uiState = ProfileUiState(),
            onEvent = {},
            onNavigateUp = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        )
    }
}
