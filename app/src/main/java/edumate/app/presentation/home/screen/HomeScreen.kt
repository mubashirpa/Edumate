package edumate.app.presentation.home.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edumate.app.R.string as Strings
import edumate.app.presentation.enrolled.screen.EnrolledScreen
import edumate.app.presentation.home.HomeTabsScreen
import edumate.app.presentation.home.HomeUiEvent
import edumate.app.presentation.home.HomeViewModel
import edumate.app.presentation.teaching.screen.TeachingScreen
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    navigateToClassDetails: (name: String?, courseId: String) -> Unit,
    navigateToCreateClass: (courseId: String?) -> Unit,
    navigateToJoinClass: () -> Unit
) {
    val tabs = listOf(
        HomeTabsScreen.Enrolled,
        HomeTabsScreen.Teaching
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
                    // TODO("Not implemented yet")
                    IconButton(onClick = { navigateToClassDetails(null, "2Mx7YrbyGUr8tsuBVr4x") }) {
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
            ) { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
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
                                    pagerState.animateScrollToPage(index)
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
                HorizontalPager(pageCount = tabs.size, state = pagerState) { page ->
                    when (page) {
                        0 -> {
                            EnrolledScreen(
                                navigateToClassDetails = navigateToClassDetails
                            )
                        }
                        1 -> {
                            TeachingScreen(
                                navigateToCreateClass = navigateToCreateClass,
                                navigateToClassDetails = navigateToClassDetails
                            )
                        }
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
                headlineContent = { Text(text = stringResource(id = Strings.create_class)) },
                modifier = Modifier.clickable {
                    viewModel.onEvent(HomeUiEvent.OnOpenFabMenuChange(false))
                    navigateToCreateClass(null)
                }
            )
            ListItem(
                headlineContent = { Text(text = stringResource(id = Strings.join_class)) },
                modifier = Modifier.clickable {
                    viewModel.onEvent(HomeUiEvent.OnOpenFabMenuChange(false))
                    navigateToJoinClass()
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}