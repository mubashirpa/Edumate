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
    ExperimentalFoundationApi::class
)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    navigateToClassDetails: (courseId: String) -> Unit,
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
                    IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(HomeUiEvent.OnOpenFabMenuChange(true))
                },
                modifier = Modifier.navigationBarsPadding()
            ) { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
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
                    val bottomMargin = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding() + 10.dp
                    val contentPadding = PaddingValues(
                        start = 10.dp,
                        top = 10.dp,
                        end = 10.dp,
                        bottom = bottomMargin
                    )

                    when (page) {
                        0 -> {
                            EnrolledScreen(
                                snackbarHostState = snackbarHostState,
                                contentPadding = contentPadding,
                                navigateToClassDetails = navigateToClassDetails
                            )
                        }

                        1 -> {
                            TeachingScreen(
                                snackbarHostState = snackbarHostState,
                                contentPadding = contentPadding,
                                navigateToCreateClass = navigateToCreateClass,
                                navigateToClassDetails = navigateToClassDetails
                            )
                        }
                    }
                }
            }

            if (viewModel.uiState.openFabMenu) {
                val bottomMargin =
                    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 10.dp

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
                    Spacer(modifier = Modifier.height(bottomMargin))
                }
            }
        }
    }
}