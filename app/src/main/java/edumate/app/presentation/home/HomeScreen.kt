package edumate.app.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import edumate.app.navigation.EdumateModalNavigationDrawer
import edumate.app.presentation.components.UserAvatar
import edumate.app.presentation.enrolled.EnrolledScreen
import edumate.app.presentation.enrolled.EnrolledViewModel
import edumate.app.presentation.home.components.AddCourseBottomSheet
import edumate.app.presentation.teaching.TeachingScreen
import edumate.app.presentation.teaching.TeachingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import edumate.app.R.string as Strings

@Composable
fun HomeScreen(
    navController: NavHostController,
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    navigateToClassDetails: (courseId: String) -> Unit,
    navigateToCreateClass: (courseId: String?) -> Unit,
    navigateToJoinClass: () -> Unit,
    navigateToProfile: () -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch {
            drawerState.close()
        }
    }

    BackHandler(enabled = uiState.showAddCourseBottomSheet) {
        onEvent(HomeUiEvent.OnShowAddCourseBottomSheetChange(false))
    }

    EdumateModalNavigationDrawer(
        navController = navController,
        drawerState = drawerState,
    ) {
        HomeScreenContent(
            uiState = uiState,
            onEvent = onEvent,
            drawerState = drawerState,
            snackbarHostState = snackbarHostState,
            navigateToClassDetails = navigateToClassDetails,
            navigateToCreateClass = navigateToCreateClass,
            navigateToJoinClass = navigateToJoinClass,
            navigateToProfile = navigateToProfile,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    navigateToClassDetails: (courseId: String) -> Unit,
    navigateToCreateClass: (courseId: String?) -> Unit,
    navigateToJoinClass: () -> Unit,
    navigateToProfile: () -> Unit,
) {
    val tabs =
        listOf(
            HomeTabsScreen.Enrolled,
            HomeTabsScreen.Teaching,
        )
    val pagerState = rememberPagerState { tabs.size }
    val coroutineScope = rememberCoroutineScope()
    val refreshScope = rememberCoroutineScope()
    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = Strings.app_name))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        UserAvatar(
                            id = uiState.currentUser?.id.orEmpty(),
                            fullName =
                                uiState.currentUser?.name?.fullName
                                    ?: uiState.currentUser?.emailAddress.orEmpty(),
                            photoUrl = uiState.currentUser?.photoUrl,
                            modifier = Modifier.clickable(onClick = navigateToProfile),
                            size = 30.dp,
                            textStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                        )
                    }
                    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                        IconButton(
                            onClick = {
                                onEvent(HomeUiEvent.OnAppBarDropdownExpandedChange(true))
                            },
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = null,
                            )
                        }
                        DropdownMenu(
                            expanded = uiState.appBarDropdownExpanded,
                            onDismissRequest = {
                                onEvent(HomeUiEvent.OnAppBarDropdownExpandedChange(false))
                            },
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(id = Strings.refresh))
                                },
                                onClick = {
                                    onEvent(HomeUiEvent.OnAppBarDropdownExpandedChange(false))
                                    onEvent(HomeUiEvent.OnRefreshChange(true))
                                    // Once the UI is refreshed, notify the ViewModel.
                                    refreshScope.launch {
                                        delay(500)
                                        onEvent(HomeUiEvent.OnRefreshChange(false))
                                    }
                                },
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(HomeUiEvent.OnShowAddCourseBottomSheetChange(true))
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(layoutDirection),
                    ),
        ) {
            PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
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
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
            ) { page ->
                when (page) {
                    0 -> {
                        val viewModel: EnrolledViewModel = hiltViewModel()
                        EnrolledScreen(
                            uiState = viewModel.uiState,
                            onEvent = viewModel::onEvent,
                            snackbarHostState = snackbarHostState,
                            innerPadding = innerPadding,
                            refreshUsingActionButton = uiState.isRefreshing,
                            navigateToClassDetails = navigateToClassDetails,
                        )
                    }

                    1 -> {
                        val viewModel: TeachingViewModel = hiltViewModel()
                        TeachingScreen(
                            uiState = viewModel.uiState,
                            onEvent = viewModel::onEvent,
                            snackbarHostState = snackbarHostState,
                            innerPadding = innerPadding,
                            refreshUsingActionButton = uiState.isRefreshing,
                            navigateToCreateClass = navigateToCreateClass,
                            navigateToClassDetails = navigateToClassDetails,
                        )
                    }
                }
            }
        }

        AddCourseBottomSheet(
            show = uiState.showAddCourseBottomSheet,
            onDismissRequest = {
                onEvent(HomeUiEvent.OnShowAddCourseBottomSheetChange(false))
            },
            innerPadding = innerPadding,
            onCreateClass = {
                navigateToCreateClass(null)
            },
            onJoinClass = navigateToJoinClass,
        )
    }
}
