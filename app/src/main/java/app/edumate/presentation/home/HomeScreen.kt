package app.edumate.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.edumate.R
import app.edumate.core.Result
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.components.ProgressDialog
import app.edumate.presentation.components.UserAvatar
import app.edumate.presentation.enrolled.EnrolledScreen
import app.edumate.presentation.home.components.AddCourseBottomSheet
import app.edumate.presentation.home.components.HomeNavigationDrawer
import app.edumate.presentation.home.components.JoinCourseBottomSheet
import app.edumate.presentation.teaching.TeachingScreen
import app.edumate.presentation.theme.EdumateTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    onNavigateToCreateCourse: (courseId: String?) -> Unit,
    onNavigateToCourseDetails: (courseId: String) -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch {
            drawerState.close()
        }
    }

    HomeNavigationDrawer(
        navController = navController,
        drawerState = drawerState,
    ) {
        HomeContent(
            uiState = viewModel.uiState,
            onEvent = viewModel::onEvent,
            joinCourseBottomSheetUiState = viewModel.joinCourseBottomSheetUiState,
            onNavigationIconClick = {
                coroutineScope.launch {
                    drawerState.open()
                }
            },
            onNavigateToCreateCourse = onNavigateToCreateCourse,
            onNavigateToClassDetails = onNavigateToCourseDetails,
            onNavigateToProfile = onNavigateToProfile,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    joinCourseBottomSheetUiState: JoinCourseBottomSheetUiState,
    onNavigationIconClick: () -> Unit,
    onNavigateToCreateCourse: (courseId: String?) -> Unit,
    onNavigateToClassDetails: (courseId: String) -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs =
        listOf(
            HomeTabScreen.Enrolled,
            HomeTabScreen.Teaching,
        )
    val layoutDirection = LocalLayoutDirection.current
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { tabs.size }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    BackHandler(enabled = uiState.showAddCourseBottomSheet) {
        onEvent(HomeUiEvent.OnShowAddCourseBottomSheetChange(false))
    }

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(HomeUiEvent.UserMessageShown)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClick) {
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
                                uiState.currentUser?.name
                                    ?: uiState.currentUser?.email.orEmpty(),
                            photoUrl = uiState.currentUser?.photoUrl,
                            modifier = Modifier.clickable(onClick = onNavigateToProfile),
                            size = 30.dp,
                            textStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                        )
                    }
                    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                        IconButton(
                            onClick = {
                                onEvent(HomeUiEvent.OnExpandedAppBarDropdownChange(true))
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                            )
                        }
                        DropdownMenu(
                            expanded = uiState.expandedAppBarDropdown,
                            onDismissRequest = {
                                onEvent(HomeUiEvent.OnExpandedAppBarDropdownChange(false))
                            },
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(id = R.string.refresh))
                                },
                                onClick = {
                                    onEvent(HomeUiEvent.OnExpandedAppBarDropdownChange(false))
                                    onEvent(HomeUiEvent.Refresh)
                                },
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
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
        val padding =
            PaddingValues(
                start = innerPadding.calculateStartPadding(layoutDirection),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateEndPadding(layoutDirection),
            )

        when (val coursesResult = uiState.coursesResult) {
            is Result.Empty -> {}

            is Result.Error -> {
                ErrorScreen(
                    onRetryClick = {
                        onEvent(HomeUiEvent.Retry)
                    },
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    errorMessage = coursesResult.message!!.asString(),
                )
            }

            is Result.Loading -> {
                LoadingScreen(modifier = Modifier.padding(innerPadding))
            }

            is Result.Success -> {
                Column(modifier = Modifier.padding(padding)) {
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
                        PullToRefreshBox(
                            isRefreshing = uiState.isRefreshing,
                            onRefresh = {
                                onEvent(HomeUiEvent.Refresh)
                            },
                        ) {
                            when (page) {
                                0 -> {
                                    EnrolledScreen(
                                        uiState = uiState,
                                        onEvent = onEvent,
                                        innerPadding = innerPadding,
                                        onNavigateToClassDetails = onNavigateToClassDetails,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }

                                1 -> {
                                    TeachingScreen(
                                        uiState = uiState,
                                        onEvent = onEvent,
                                        innerPadding = innerPadding,
                                        onNavigateToCreateCourse = onNavigateToCreateCourse,
                                        onNavigateToClassDetails = onNavigateToClassDetails,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    AddCourseBottomSheet(
        show = uiState.showAddCourseBottomSheet,
        onDismissRequest = {
            onEvent(HomeUiEvent.OnShowAddCourseBottomSheetChange(false))
        },
        onCreateClass = {
            onNavigateToCreateCourse(null)
        },
        onJoinClass = {
            onEvent(HomeUiEvent.OnShowJoinCourseBottomSheetChange(true))
        },
    )

    JoinCourseBottomSheet(
        uiState = joinCourseBottomSheetUiState,
        show = uiState.showJoinCourseBottomSheet,
        user = uiState.currentUser,
        onDismissRequest = {
            onEvent(HomeUiEvent.OnShowJoinCourseBottomSheetChange(false))
        },
        onJoinCourse = { courseId ->
            onEvent(HomeUiEvent.JoinCourse(courseId))
        },
    )

    ProgressDialog(
        open = uiState.openProgressDialog,
        onDismissRequest = {},
    )
}

@Preview
@Composable
private fun HomeScreenPreview() {
    EdumateTheme {
        HomeContent(
            uiState = HomeUiState(),
            onEvent = {},
            joinCourseBottomSheetUiState = JoinCourseBottomSheetUiState(),
            onNavigationIconClick = {},
            onNavigateToCreateCourse = {},
            onNavigateToClassDetails = {},
            onNavigateToProfile = {},
        )
    }
}
