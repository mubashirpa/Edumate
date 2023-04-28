package edumate.app.presentation.view_classwork.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.core.DataState
import edumate.app.core.ext.header
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.User
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.components.ComingSoon
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.student_work.screen.StudentWorkScreen
import edumate.app.presentation.view_classwork.ViewClassworkTabsScreen
import edumate.app.presentation.view_classwork.ViewClassworkUiEvent
import edumate.app.presentation.view_classwork.ViewClassworkUiState
import edumate.app.presentation.view_classwork.screen.components.AttachmentsListItem
import edumate.app.presentation.view_classwork.screen.components.RemoveAttachmentDialog
import edumate.app.presentation.view_classwork.screen.components.TurnInDialog
import edumate.app.presentation.view_classwork.screen.components.UnSubmitDialog
import edumate.app.presentation.view_classwork.screen.components.YourWorkBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ViewClassworkScreen(
    uiState: ViewClassworkUiState,
    onEvent: (ViewClassworkUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    classworkType: CourseWorkType,
    currentUserType: UserType,
    navigateToViewStudentWork: (
        classwork: CourseWork,
        studentWorkId: String?,
        assignedStudent: User
    ) -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val refreshState = rememberPullRefreshState(
        refreshing = uiState.refreshing,
        onRefresh = {
            onEvent(ViewClassworkUiEvent.OnRefresh)
        }
    )

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(ViewClassworkUiEvent.UserMessageShown)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = Strings.navigate_up)
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { share(context, uiState.classwork.alternateLink) }
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null
                    )
                }
                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                    if (currentUserType == UserType.TEACHER) {
                        IconButton(
                            onClick = {
                                onEvent(
                                    ViewClassworkUiEvent.OnAppBarMenuExpandedChange(
                                        true
                                    )
                                )
                            }
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = null
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = uiState.appBarMenuExpanded,
                        onDismissRequest = {
                            onEvent(ViewClassworkUiEvent.OnAppBarMenuExpandedChange(false))
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = Strings.refresh)) },
                            onClick = {
                                onEvent(ViewClassworkUiEvent.OnAppBarMenuExpandedChange(false))
                                onEvent(ViewClassworkUiEvent.OnRefresh)
                            }
                        )
                        // TODO("Add edit and delete for teachers")
                    }
                }
            }
        )
        when (val dataState = uiState.dataState) {
            is DataState.EMPTY -> {
                ErrorScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    errorMessage = dataState.message.asString()
                )
            }

            is DataState.ERROR -> {
                ErrorScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    errorMessage = dataState.message.asString()
                )
            }

            DataState.LOADING -> {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                )
            }

            DataState.SUCCESS -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(refreshState)
                ) {
                    when (classworkType) {
                        CourseWorkType.MATERIAL -> {
                            ContentMaterial(uiState = uiState)
                        }

                        CourseWorkType.ASSIGNMENT -> {
                            ContentAssignment(
                                uiState = uiState,
                                onEvent = onEvent,
                                currentUserType = currentUserType,
                                navigateToViewStudentWork = navigateToViewStudentWork
                            )
                        }

                        CourseWorkType.SHORT_ANSWER_QUESTION -> {
                            // TODO()
                            ComingSoon(modifier = Modifier.navigationBarsPadding())
                        }

                        CourseWorkType.MULTIPLE_CHOICE_QUESTION -> {
                            // TODO()
                            ComingSoon(modifier = Modifier.navigationBarsPadding())
                        }

                        else -> {}
                    }

                    PullRefreshIndicator(
                        uiState.refreshing,
                        refreshState,
                        Modifier.align(Alignment.TopCenter)
                    )
                }
            }

            DataState.UNKNOWN -> {}
        }
    }

    TurnInDialog(
        uiState = uiState,
        onDismissRequest = {
            onEvent(ViewClassworkUiEvent.OnOpenTurnInDialog(false))
        },
        onConfirmClick = {
            onEvent(ViewClassworkUiEvent.TurnIn)
        }
    )

    UnSubmitDialog(
        uiState = uiState,
        onDismissRequest = {
            onEvent(ViewClassworkUiEvent.OnOpenUnSubmitDialog(false))
        },
        onConfirmClick = {
            onEvent(ViewClassworkUiEvent.UnSubmit)
        }
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
}

@Composable
private fun ContentMaterial(uiState: ViewClassworkUiState) {
    val context = LocalContext.current
    val navigationBarHeight =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomMargin = navigationBarHeight + 10.dp
    val contentPadding = PaddingValues(
        start = 16.dp,
        top = 10.dp,
        end = 16.dp,
        bottom = bottomMargin
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        content = {
            header {
                Text(
                    text = uiState.classwork.title,
                    modifier = Modifier.padding(top = 6.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            val description = uiState.classwork.description
            if (description != null) {
                header {
                    Text(
                        text = description,
                        modifier = Modifier.padding(top = 6.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            val attachments = uiState.classwork.materials
            if (attachments.isNotEmpty()) {
                header {
                    Text(
                        text = stringResource(id = Strings.attachments),
                        modifier = Modifier.padding(top = 6.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(attachments) {
                    AttachmentsListItem(
                        attachment = it,
                        onClick = { url ->
                            if (url != null) {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(browserIntent)
                            }
                        }
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContentAssignment(
    uiState: ViewClassworkUiState,
    onEvent: (ViewClassworkUiEvent) -> Unit,
    currentUserType: UserType,
    navigateToViewStudentWork: (
        classwork: CourseWork,
        studentWorkId: String?,
        assignedStudent: User
    ) -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val fileUtils = remember { FileUtils(context) }
    val tabs = listOf(
        ViewClassworkTabsScreen.Instructions,
        ViewClassworkTabsScreen.StudentWork
    )
    val contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    val isTeacher = currentUserType == UserType.TEACHER
    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                onEvent(ViewClassworkUiEvent.OnFilePicked(it, fileUtils))
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (isTeacher) {
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
            }
            HorizontalPager(
                pageCount = tabs.size,
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                userScrollEnabled = isTeacher
            ) { page ->
                when (page) {
                    0 -> {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 128.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = contentPadding,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            content = {
                                header {
                                    Column {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        val dueDate = uiState.classwork.dueTime
                                        if (dueDate != null) {
                                            val date =
                                                DateUtils.getRelativeTimeSpanString(
                                                    dueDate.time
                                                )
                                            Text(
                                                text = stringResource(
                                                    id = Strings.due_,
                                                    date.toString()
                                                ),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                        }
                                        Text(
                                            text = uiState.classwork.title,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.headlineSmall
                                        )
                                        val points = uiState.classwork.maxPoints
                                        if (points != null && points > 0) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = stringResource(
                                                    id = Strings._points,
                                                    points
                                                ),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                                val description = uiState.classwork.description
                                if (description != null) {
                                    header {
                                        Text(
                                            text = description,
                                            modifier = Modifier.padding(top = 6.dp),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                                val attachments = uiState.classwork.materials
                                if (attachments.isNotEmpty()) {
                                    header {
                                        Text(
                                            text = stringResource(id = Strings.attachments),
                                            modifier = Modifier.padding(top = 6.dp),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    items(attachments) {
                                        AttachmentsListItem(
                                            attachment = it,
                                            onClick = { url ->
                                                if (url != null) {
                                                    val browserIntent =
                                                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                    context.startActivity(browserIntent)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }

                    1 -> {
                        // TODO("Fix: Refresh only working if page == 0")
                        StudentWorkScreen(
                            courseWork = uiState.classwork,
                            navigateToViewStudentWork = { studentWorkId, assignedStudent ->
                                navigateToViewStudentWork(
                                    uiState.classwork,
                                    studentWorkId,
                                    assignedStudent
                                )
                            }
                        )
                    }
                }
            }
        }

        if (currentUserType == UserType.STUDENT) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Button(
                    onClick = {
                        onEvent(ViewClassworkUiEvent.OnOpenYourWorkBottomSheet(true))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = Strings.your_work))
                }
            }
        }
    }

    YourWorkBottomSheet(
        uiState = uiState,
        onDismissRequest = {
            onEvent(ViewClassworkUiEvent.OnOpenYourWorkBottomSheet(false))
        },
        onAddAttachmentClick = {
            filePicker.launch("*/*")
        },
        onRemoveAttachmentClick = {
            onEvent(ViewClassworkUiEvent.OnOpenRemoveAttachmentDialog(it))
        },
        onSubmitClick = {
            onEvent(ViewClassworkUiEvent.OnOpenTurnInDialog(true))
        },
        onUnSubmitClick = {
            onEvent(ViewClassworkUiEvent.OnOpenUnSubmitDialog(true))
        }
    )

    RemoveAttachmentDialog(
        uiState = uiState,
        onDismissRequest = {
            onEvent(ViewClassworkUiEvent.OnOpenRemoveAttachmentDialog(null))
        },
        onConfirmClick = {
            onEvent(ViewClassworkUiEvent.OnRemoveAttachment(it))
        }
    )
}

private fun share(context: Context, text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}