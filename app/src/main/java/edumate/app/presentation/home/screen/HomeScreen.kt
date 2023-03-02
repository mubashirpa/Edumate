package edumate.app.presentation.home.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import edumate.app.R.string as Strings
import edumate.app.presentation.home.HomeUiEvent
import edumate.app.presentation.home.HomeViewModel
import edumate.app.presentation.home.screen.components.EnrolledContent
import edumate.app.presentation.home.screen.components.TabScreen
import edumate.app.presentation.home.screen.components.TeachingContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToCreateRoom: () -> Unit
) {
    val tabs = listOf(
        TabScreen.Enrolled,
        TabScreen.Teaching
    )
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    BackHandler(viewModel.uiState.openFabMenu) {
        viewModel.onEvent(HomeUiEvent.OnOpenFabMenuChange(false))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = Strings.app_name))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(HomeUiEvent.SignOut)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(HomeUiEvent.OnOpenFabMenuChange(true))
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(selectedTabIndex = pagerState.currentPage) {
                    tabs.forEachIndexed { index, screen ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(
                                        index
                                    )
                                }
                            },
                            text = {
                                Text(
                                    text = stringResource(id = screen.title),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }

                HorizontalPager(count = tabs.size, state = pagerState) { page ->
                    when (page) {
                        0 -> {
                            EnrolledContent(
                                isLoading = viewModel.uiState.loading,
                                error = viewModel.uiState.errorMessage,
                                rooms = viewModel.uiState.rooms
                            )
                        }
                        1 -> TeachingContent()
                    }
                }
            }
        }
    }

    if (viewModel.uiState.openFabMenu) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.onEvent(HomeUiEvent.OnOpenFabMenuChange(false))
            }
        ) {
            ListItem(
                headlineText = { Text(text = "Create room") },
                modifier = Modifier.clickable {
                    viewModel.onEvent(HomeUiEvent.OnOpenFabMenuChange(false))
                    navigateToCreateRoom()
                }
            )
            ListItem(
                headlineText = { Text(text = "Join room") },
                modifier = Modifier.clickable {
                    viewModel.onEvent(HomeUiEvent.OnOpenFabMenuChange(false))
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}