package edumate.app.presentation.view_student_work.screen

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.core.DataState
import edumate.app.core.ext.header
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.domain.model.student_submissions.SubmissionState
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.components.UserAvatar
import edumate.app.presentation.view_student_work.ViewStudentWorkUiEvent
import edumate.app.presentation.view_student_work.ViewStudentWorkUiState
import edumate.app.presentation.view_student_work.screen.components.AttachmentsListItem
import edumate.app.presentation.view_student_work.screen.components.ReturnDialog
import java.util.Date

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun ViewStudentWorkScreen(
    uiState: ViewStudentWorkUiState,
    onEvent: (ViewStudentWorkUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    courseWork: CourseWork,
    assignedStudent: UserProfile,
    onBackPressed: () -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val context = LocalContext.current
    val refreshState = rememberPullRefreshState(
        refreshing = uiState.refreshing,
        onRefresh = { onEvent(ViewStudentWorkUiEvent.OnRefresh) }
    )
    val isCourseWorkMarked = courseWork.maxPoints != null && courseWork.maxPoints > 0

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(ViewStudentWorkUiEvent.UserMessageShown)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        TopAppBar(
            title = { Text(text = courseWork.title) },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = Strings.navigate_up)
                    )
                }
            },
            actions = {
                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                    IconButton(
                        onClick = { onEvent(ViewStudentWorkUiEvent.OnAppBarMenuExpandedChange(true)) }
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                    DropdownMenu(
                        expanded = uiState.appBarMenuExpanded,
                        onDismissRequest = {
                            onEvent(ViewStudentWorkUiEvent.OnAppBarMenuExpandedChange(false))
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = Strings.refresh)) },
                            onClick = {
                                onEvent(ViewStudentWorkUiEvent.OnAppBarMenuExpandedChange(false))
                                onEvent(ViewStudentWorkUiEvent.OnRefresh)
                            }
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(refreshState)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentAlignment = Alignment.TopCenter
        ) {
            when (val dataState = uiState.dataState) {
                is DataState.EMPTY -> {
                    ErrorScreen(
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = dataState.message.asString()
                    )
                }

                is DataState.ERROR -> {
                    ErrorScreen(
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = dataState.message.asString()
                    )
                }

                DataState.LOADING -> {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }

                DataState.SUCCESS -> {
                    val focusManager = LocalFocusManager.current
                    val keyboardController = LocalSoftwareKeyboardController.current
                    val dueDate = courseWork.dueTime
                    val attachments = uiState.studentWork?.assignmentSubmission?.attachments
                    val isReturnEnabled =
                        if (isCourseWorkMarked) {
                            uiState.grade.isNotBlank() && (uiState.studentWork?.state == SubmissionState.TURNED_IN || uiState.studentWork?.assignedGrade.toString() != uiState.grade)
                        } else {
                            uiState.studentWork?.state == SubmissionState.TURNED_IN
                        }

                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 128.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            content = {
                                header {
                                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                        UserAvatar(
                                            id = assignedStudent.id,
                                            fullName = assignedStudent.displayName
                                                ?: assignedStudent.emailAddress.orEmpty(),
                                            photoUrl = assignedStudent.photoUrl
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = assignedStudent.displayName
                                                    ?: assignedStudent.emailAddress.orEmpty(),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                style = MaterialTheme.typography.bodyLarge
                                            )

                                            when {
                                                isCourseWorkMarked && uiState.studentWork?.assignedGrade != null -> {
                                                    Text(
                                                        text = stringResource(id = Strings.marked),
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }

                                                uiState.studentWork?.state == SubmissionState.TURNED_IN -> {
                                                    Text(
                                                        text = stringResource(
                                                            id = Strings.handed_in
                                                        ),
                                                        color = MaterialTheme.colorScheme.primary,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }

                                                uiState.studentWork?.state == SubmissionState.RETURNED -> {
                                                    Text(
                                                        text = stringResource(id = Strings.returned),
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }

                                                dueDate?.before(Date()) == true -> {
                                                    Text(
                                                        text = stringResource(id = Strings.missing),
                                                        color = MaterialTheme.colorScheme.error,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }

                                                else -> {
                                                    Text(
                                                        text = stringResource(id = Strings.assigned),
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                when (courseWork.workType) {
                                    CourseWorkType.ASSIGNMENT -> {
                                        if (attachments.isNullOrEmpty()) {
                                            header {
                                                ErrorScreen(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(128.dp),
                                                    errorMessage = stringResource(
                                                        id = Strings.no_files_attached
                                                    )
                                                )
                                            }
                                        } else {
                                            header {
                                                Text(
                                                    text = stringResource(id = Strings.attachments),
                                                    modifier = Modifier.padding(
                                                        top = 14.dp,
                                                        bottom = 6.dp
                                                    ),
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            }
                                            items(attachments) {
                                                AttachmentsListItem(
                                                    attachment = it,
                                                    onClick = { url ->
                                                        if (url != null) {
                                                            val browserIntent = Intent(
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

                                    CourseWorkType.SHORT_ANSWER_QUESTION -> {
                                        val submission = uiState.studentWork?.shortAnswerSubmission
                                        header {
                                            Column {
                                                Text(
                                                    text = stringResource(id = Strings.answer),
                                                    modifier = Modifier.padding(
                                                        top = 14.dp,
                                                        bottom = 16.dp
                                                    ),
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                if (submission != null) {
                                                    Text(text = submission.answer)
                                                } else {
                                                    ErrorScreen(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(128.dp),
                                                        errorMessage = stringResource(
                                                            id = Strings.the_student_hasnt_answered_the_question_yet
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    CourseWorkType.MULTIPLE_CHOICE_QUESTION -> {
                                        val submission =
                                            uiState.studentWork?.multipleChoiceSubmission
                                        header {
                                            Column {
                                                Text(
                                                    text = stringResource(id = Strings.answer),
                                                    modifier = Modifier.padding(
                                                        top = 14.dp,
                                                        bottom = 16.dp
                                                    ),
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                if (courseWork.multipleChoiceQuestion != null) {
                                                    courseWork.multipleChoiceQuestion.choices.forEach { text ->
                                                        Row(
                                                            Modifier
                                                                .fillMaxWidth()
                                                                .height(56.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            RadioButton(
                                                                selected = submission != null && text == submission.answer,
                                                                onClick = null // null recommended for accessibility with screen readers
                                                            )
                                                            Text(
                                                                text = text,
                                                                style = MaterialTheme.typography.bodyLarge,
                                                                modifier = Modifier.padding(
                                                                    start = 16.dp
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    else -> {}
                                }
                            }
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .imePadding(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isCourseWorkMarked) {
                                OutlinedTextField(
                                    value = uiState.grade,
                                    onValueChange = {
                                        onEvent(ViewStudentWorkUiEvent.OnGradeChange(it))
                                    },
                                    modifier = Modifier.weight(1f),
                                    label = { Text(text = stringResource(id = Strings.mark)) },
                                    suffix = { Text(text = "/${courseWork.maxPoints}") },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                        }
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Button(
                                onClick = { onEvent(ViewStudentWorkUiEvent.OnOpenReturnDialog(true)) },
                                modifier = Modifier.padding(top = 8.dp),
                                enabled = isReturnEnabled
                            ) { Text(text = stringResource(id = Strings._return)) }
                        }
                    }
                }

                DataState.UNKNOWN -> {}
            }

            PullRefreshIndicator(
                uiState.refreshing,
                refreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }

    ReturnDialog(
        onDismissRequest = { onEvent(ViewStudentWorkUiEvent.OnOpenReturnDialog(false)) },
        uiState = uiState,
        courseWork = courseWork,
        userName = assignedStudent.displayName ?: assignedStudent.emailAddress.orEmpty(),
        onConfirmClick = {
            if (isCourseWorkMarked) {
                onEvent(ViewStudentWorkUiEvent.PatchStudentWork)
            } else {
                onEvent(ViewStudentWorkUiEvent.ReturnStudentWork)
            }
        }
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
}