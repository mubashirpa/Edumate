package edumate.app.presentation.view_classwork.screen.components

import android.content.Intent
import android.net.Uri
import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.core.ext.header
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.student_work.screen.StudentWorkScreen
import edumate.app.presentation.view_classwork.ViewClassworkTabsScreen
import edumate.app.presentation.view_classwork.ViewClassworkUiEvent
import edumate.app.presentation.view_classwork.ViewClassworkUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ContentAssignment(
    uiState: ViewClassworkUiState,
    onEvent: (ViewClassworkUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    currentUserType: UserType,
    navigateToViewStudentWork: (
        classwork: CourseWork,
        studentWorkId: String?,
        assignedStudent: UserProfile
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
                        val contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                        val refreshState = rememberPullRefreshState(
                            refreshing = uiState.refreshing,
                            onRefresh = { onEvent(ViewClassworkUiEvent.OnRefresh) }
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pullRefresh(refreshState)
                        ) {
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
                                                            Intent(
                                                                Intent.ACTION_VIEW,
                                                                Uri.parse(url)
                                                            )
                                                        context.startActivity(browserIntent)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            )

                            PullRefreshIndicator(
                                uiState.refreshing,
                                refreshState,
                                Modifier.align(Alignment.TopCenter)
                            )
                        }
                    }

                    1 -> {
                        StudentWorkScreen(
                            snackbarHostState = snackbarHostState,
                            courseWork = uiState.classwork,
                            refreshUsingActionButton = uiState.refreshing,
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
        onDismissRequest = {
            onEvent(ViewClassworkUiEvent.OnOpenRemoveAttachmentDialog(null))
        },
        uiState = uiState,
        onConfirmClick = {
            onEvent(ViewClassworkUiEvent.OnRemoveAttachment(it))
        }
    )
}