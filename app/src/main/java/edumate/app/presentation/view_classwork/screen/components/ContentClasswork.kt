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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edumate.app.core.DataState
import edumate.app.core.ext.header
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.student_work.screen.StudentWorkScreen
import edumate.app.presentation.view_classwork.ViewClassworkTabsScreen
import edumate.app.presentation.view_classwork.ViewClassworkUiEvent
import edumate.app.presentation.view_classwork.ViewClassworkUiState
import kotlinx.coroutines.launch
import edumate.app.R.string as Strings

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ContentClasswork(
    uiState: ViewClassworkUiState,
    onEvent: (ViewClassworkUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    classworkType: CourseWorkType,
    currentUserType: UserType,
    modifier: Modifier = Modifier,
    navigateToViewStudentWork: (
        classwork: CourseWork,
        studentWorkId: String?,
        assignedStudent: UserProfile,
    ) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fileUtils = remember { FileUtils(context) }
    val isTeacher = currentUserType == UserType.TEACHER
    val tabs =
        when (classworkType) {
            CourseWorkType.ASSIGNMENT -> {
                listOf(
                    ViewClassworkTabsScreen.Instructions,
                    ViewClassworkTabsScreen.StudentWork,
                )
            }

            CourseWorkType.SHORT_ANSWER_QUESTION -> {
                listOf(
                    ViewClassworkTabsScreen.Question,
                    ViewClassworkTabsScreen.StudentAnswers,
                )
            }

            CourseWorkType.MULTIPLE_CHOICE_QUESTION -> {
                listOf(
                    ViewClassworkTabsScreen.Question,
                    ViewClassworkTabsScreen.StudentAnswers,
                )
            }

            else -> {
                emptyList()
            }
        }
    val pagerState = rememberPagerState { if (isTeacher) tabs.size else 1 }
    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                onEvent(ViewClassworkUiEvent.OnFilePicked(it, fileUtils))
            }
        }
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        TabRowDefaults.PrimaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
            width = tabPositions[pagerState.currentPage].contentWidth,
        )
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .navigationBarsPadding(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) {
            if (isTeacher) {
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
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = isTeacher,
            ) { page ->
                when (page) {
                    0 -> {
                        val contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                        val refreshState =
                            rememberPullRefreshState(
                                refreshing = uiState.refreshing,
                                onRefresh = { onEvent(ViewClassworkUiEvent.OnRefresh) },
                            )

                        Box(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .pullRefresh(refreshState),
                        ) {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 128.dp),
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .imePadding(),
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
                                                        dueDate.time,
                                                    )
                                                Text(
                                                    text =
                                                        stringResource(
                                                            id = Strings.due_,
                                                            date.toString(),
                                                        ),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                            Text(
                                                text = uiState.classwork.title,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                style = MaterialTheme.typography.headlineSmall,
                                            )
                                            val points = uiState.classwork.maxPoints
                                            if (points != null && points > 0) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text =
                                                        stringResource(
                                                            id = Strings._points,
                                                            points,
                                                        ),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    style = MaterialTheme.typography.bodyMedium,
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
                                                style = MaterialTheme.typography.bodyLarge,
                                            )
                                        }
                                    }
                                    val attachments = uiState.classwork.materials
                                    if (attachments.isNotEmpty()) {
                                        header {
                                            Text(
                                                text = stringResource(id = Strings.attachments),
                                                modifier =
                                                    Modifier.padding(
                                                        top = 14.dp,
                                                        bottom = 6.dp,
                                                    ),
                                                style = MaterialTheme.typography.titleMedium,
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
                                                                Uri.parse(url),
                                                            )
                                                        context.startActivity(browserIntent)
                                                    }
                                                },
                                            )
                                        }
                                    }
                                    shortAnswerContent(
                                        uiState = uiState,
                                        onEvent = onEvent,
                                        classworkType = classworkType,
                                        isTeacher = isTeacher,
                                    )
                                    multipleChoiceContent(
                                        uiState = uiState,
                                        onEvent = onEvent,
                                        classworkType = classworkType,
                                        isTeacher = isTeacher,
                                    )
                                },
                            )

                            PullRefreshIndicator(
                                uiState.refreshing,
                                refreshState,
                                Modifier.align(Alignment.TopCenter),
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
                                    assignedStudent,
                                )
                            },
                        )
                    }
                }
            }
        }

        if (!isTeacher && classworkType == CourseWorkType.ASSIGNMENT) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
            ) {
                Button(
                    onClick = {
                        onEvent(ViewClassworkUiEvent.OnOpenYourWorkBottomSheet(true))
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(id = Strings.your_work))
                }
            }
        }
    }

    YourWorkBottomSheet(
        uiState = uiState,
        onDismissRequest = { onEvent(ViewClassworkUiEvent.OnOpenYourWorkBottomSheet(false)) },
        onAddAttachmentClick = { filePicker.launch("*/*") },
        onRemoveAttachmentClick = { onEvent(ViewClassworkUiEvent.OnOpenRemoveAttachmentDialog(it)) },
        onSubmitClick = { onEvent(ViewClassworkUiEvent.OnOpenTurnInDialog(true)) },
        onUnSubmitClick = { onEvent(ViewClassworkUiEvent.OnOpenUnSubmitDialog(true)) },
    )

    TurnInDialog(
        onDismissRequest = { onEvent(ViewClassworkUiEvent.OnOpenTurnInDialog(false)) },
        uiState = uiState,
        onConfirmClick = { onEvent(ViewClassworkUiEvent.TurnIn) },
    )

    HandInDialog(
        onDismissRequest = { onEvent(ViewClassworkUiEvent.OnOpenHandInDialog(false)) },
        uiState = uiState,
        onConfirmClick = { onEvent(ViewClassworkUiEvent.TurnIn) },
    )

    UnSubmitDialog(
        onDismissRequest = { onEvent(ViewClassworkUiEvent.OnOpenUnSubmitDialog(false)) },
        uiState = uiState,
        onConfirmClick = { onEvent(ViewClassworkUiEvent.UnSubmit) },
    )

    RemoveAttachmentDialog(
        onDismissRequest = { onEvent(ViewClassworkUiEvent.OnOpenRemoveAttachmentDialog(null)) },
        uiState = uiState,
        onConfirmClick = { onEvent(ViewClassworkUiEvent.OnRemoveAttachment(it)) },
    )
}

private fun LazyGridScope.shortAnswerContent(
    uiState: ViewClassworkUiState,
    onEvent: (ViewClassworkUiEvent) -> Unit,
    classworkType: CourseWorkType,
    isTeacher: Boolean,
) {
    if (!isTeacher && classworkType == CourseWorkType.SHORT_ANSWER_QUESTION) {
        header {
            Column {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 14.dp,
                                bottom = 16.dp,
                            ),
                ) {
                    Text(
                        text =
                            stringResource(
                                id = Strings.your_answer,
                            ),
                        modifier =
                            Modifier
                                .padding(end = 16.dp)
                                .weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    DueText(uiState = uiState)
                }
                when (uiState.yourWorkDataState) {
                    is DataState.EMPTY -> {
                        ErrorScreen(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(128.dp),
                            errorMessage = uiState.yourWorkDataState.message.asString(),
                        )
                    }

                    is DataState.ERROR -> {
                        ErrorScreen(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(128.dp),
                            errorMessage = uiState.yourWorkDataState.message.asString(),
                        )
                    }

                    DataState.LOADING -> {
                        LoadingIndicator(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(128.dp),
                        )
                    }

                    DataState.SUCCESS -> {
                        if (uiState.studentSubmission?.shortAnswerSubmission?.answer == null || uiState.editShortAnswer) {
                            TextField(
                                value = uiState.shortAnswer,
                                onValueChange = {
                                    onEvent(
                                        ViewClassworkUiEvent.OnShortAnswerChange(
                                            it,
                                        ),
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = {
                                    Text(
                                        text =
                                            stringResource(
                                                id = Strings.type_your_answer,
                                            ),
                                    )
                                },
                                keyboardOptions =
                                    KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Sentences,
                                        autoCorrect = true,
                                    ),
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    onEvent(ViewClassworkUiEvent.TurnIn)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = uiState.shortAnswer.isNotEmpty(),
                            ) {
                                Text(
                                    text =
                                        stringResource(
                                            id = Strings.hand_in,
                                        ),
                                )
                            }
                        } else {
                            Text(
                                text = uiState.studentSubmission.shortAnswerSubmission.answer,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = {
                                    onEvent(
                                        ViewClassworkUiEvent.OnEditShortAnswerChange(
                                            true,
                                        ),
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = uiState.shortAnswer.isNotEmpty(),
                            ) {
                                Text(
                                    text =
                                        stringResource(
                                            id = Strings.edit,
                                        ),
                                )
                            }
                        }
                    }

                    DataState.UNKNOWN -> {}
                }
            }
        }
    }
}

private fun LazyGridScope.multipleChoiceContent(
    uiState: ViewClassworkUiState,
    onEvent: (ViewClassworkUiEvent) -> Unit,
    classworkType: CourseWorkType,
    isTeacher: Boolean,
) {
    if (!isTeacher && classworkType == CourseWorkType.MULTIPLE_CHOICE_QUESTION) {
        header {
            Column {
                Row(
                    modifier =
                        Modifier.padding(
                            top = 14.dp,
                            bottom = 16.dp,
                        ),
                ) {
                    Text(
                        text =
                            stringResource(
                                id = Strings.your_answer,
                            ),
                        modifier =
                            Modifier
                                .padding(end = 16.dp)
                                .weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    DueText(uiState = uiState)
                }
                when (uiState.yourWorkDataState) {
                    is DataState.EMPTY -> {
                        ErrorScreen(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(128.dp),
                            errorMessage = uiState.yourWorkDataState.message.asString(),
                        )
                    }

                    is DataState.ERROR -> {
                        ErrorScreen(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(128.dp),
                            errorMessage = uiState.yourWorkDataState.message.asString(),
                        )
                    }

                    DataState.LOADING -> {
                        LoadingIndicator(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(128.dp),
                        )
                    }

                    DataState.SUCCESS -> {
                        val choices =
                            uiState.classwork.multipleChoiceQuestion?.choices.orEmpty()
                        val choiceSelectable =
                            uiState.studentSubmission?.multipleChoiceSubmission?.answer == null
                        Column(modifier = Modifier.selectableGroup()) {
                            choices.forEach { text ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .selectable(
                                            selected = (text == uiState.multipleChoiceAnswer),
                                            enabled = choiceSelectable,
                                            role = Role.RadioButton,
                                            onClick = {
                                                onEvent(
                                                    ViewClassworkUiEvent.OnMultipleChoiceAnswerChange(
                                                        text,
                                                    ),
                                                )
                                            },
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    RadioButton(
                                        selected = (text == uiState.multipleChoiceAnswer),
                                        onClick = null, // null recommended for accessibility with screen readers
                                    )
                                    Text(
                                        text = text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier =
                                            Modifier.padding(
                                                start = 16.dp,
                                            ),
                                    )
                                }
                            }
                        }
                        if (choiceSelectable) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    onEvent(
                                        ViewClassworkUiEvent.OnOpenHandInDialog(
                                            true,
                                        ),
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = uiState.multipleChoiceAnswer.isNotEmpty(),
                            ) {
                                Text(
                                    text =
                                        stringResource(
                                            id = Strings.hand_in,
                                        ),
                                )
                            }
                        }
                    }

                    DataState.UNKNOWN -> {}
                }
            }
        }
    }
}
