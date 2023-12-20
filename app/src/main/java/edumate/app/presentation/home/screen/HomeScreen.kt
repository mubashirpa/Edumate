package edumate.app.presentation.home.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edumate.app.presentation.components.UserAvatar
import edumate.app.presentation.enrolled.screen.EnrolledScreen
import edumate.app.presentation.home.HomeTabsScreen
import edumate.app.presentation.home.HomeUiEvent
import edumate.app.presentation.home.HomeUiState
import edumate.app.presentation.teaching.screen.TeachingScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import edumate.app.R.string as Strings

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
)
@Composable
fun HomeScreen(
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
    val bottomSheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        TabRowDefaults.PrimaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
            width = tabPositions[pagerState.currentPage].contentWidth,
        )
    }

    BackHandler(uiState.openFabMenu) {
        onEvent(HomeUiEvent.OnOpenFabMenuChange(false))
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
                },
                actions = {
                    Box(
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        UserAvatar(
                            id = uiState.currentUser?.uid.orEmpty(),
                            fullName =
                                uiState.currentUser?.displayName
                                    ?: uiState.currentUser?.email.orEmpty(),
                            photoUri = uiState.currentUser?.photoUrl,
                            modifier = Modifier.clickable(onClick = navigateToProfile),
                            size = 30.dp,
                            textStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                        )
                    }
                    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                        IconButton(
                            onClick = {
                                onEvent(HomeUiEvent.OnAppBarMenuExpandedChange(true))
                            },
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = null,
                            )
                        }
                        DropdownMenu(
                            expanded = uiState.appBarMenuExpanded,
                            onDismissRequest = {
                                onEvent(HomeUiEvent.OnAppBarMenuExpandedChange(false))
                            },
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = Strings.refresh)) },
                                onClick = {
                                    onEvent(HomeUiEvent.OnAppBarMenuExpandedChange(false))
                                    onEvent(HomeUiEvent.OnRefreshChange(true))
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
                onClick = { onEvent(HomeUiEvent.OnOpenFabMenuChange(true)) },
                modifier = Modifier.navigationBarsPadding(),
            ) { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
        },
        contentWindowInsets =
            ScaffoldDefaults
                .contentWindowInsets
                .exclude(WindowInsets.navigationBars),
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = indicator,
                ) {
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
                ) { page ->
                    val bottomMargin =
                        WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding() + 88.dp
                    val contentPadding =
                        PaddingValues(
                            start = 10.dp,
                            top = 10.dp,
                            end = 10.dp,
                            bottom = bottomMargin,
                        )

                    when (page) {
                        0 -> {
                            EnrolledScreen(
                                snackbarHostState = snackbarHostState,
                                contentPadding = contentPadding,
                                refreshUsingActionButton = uiState.refreshing,
                                navigateToClassDetails = navigateToClassDetails,
                            )
                        }

                        1 -> {
                            TeachingScreen(
                                snackbarHostState = snackbarHostState,
                                contentPadding = contentPadding,
                                refreshUsingActionButton = uiState.refreshing,
                                navigateToCreateClass = navigateToCreateClass,
                                navigateToClassDetails = navigateToClassDetails,
                            )
                        }
                    }
                }
            }

            if (uiState.openFabMenu) {
                val bottomMargin =
                    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 10.dp

                ModalBottomSheet(
                    onDismissRequest = {
                        onEvent(HomeUiEvent.OnOpenFabMenuChange(false))
                    },
                    sheetState = bottomSheetState,
                    windowInsets = WindowInsets(0),
                ) {
                    ListItem(
                        headlineContent = { Text(text = stringResource(id = Strings.create_class)) },
                        modifier =
                            Modifier.clickable {
                                onEvent(HomeUiEvent.OnOpenFabMenuChange(false))
                                navigateToCreateClass(null)
                            },
                    )
                    ListItem(
                        headlineContent = { Text(text = stringResource(id = Strings.join_class)) },
                        modifier =
                            Modifier.clickable {
                                onEvent(HomeUiEvent.OnOpenFabMenuChange(false))
                                navigateToJoinClass()
                            },
                    )
                    Spacer(modifier = Modifier.height(bottomMargin))
                }
            }
        }
    }
}
