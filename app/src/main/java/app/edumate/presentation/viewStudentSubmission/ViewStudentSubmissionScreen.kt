package app.edumate.presentation.viewStudentSubmission

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.ext.header
import app.edumate.core.utils.DateTimeUtils
import app.edumate.core.utils.FileType
import app.edumate.core.utils.FileUtils
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.domain.model.studentSubmission.StudentSubmission
import app.edumate.domain.model.studentSubmission.SubmissionState
import app.edumate.presentation.components.AttachmentsListItem
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.components.ProgressDialog
import app.edumate.presentation.components.UserAvatar
import app.edumate.presentation.viewStudentSubmission.components.GradeBottomBar
import app.edumate.presentation.viewStudentSubmission.components.ReturnDialog
import kotlinx.datetime.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewStudentSubmissionScreen(
    uiState: ViewStudentSubmissionUiState,
    onEvent: (ViewStudentSubmissionUiEvent) -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToImageViewer: (url: String, title: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val studentSubmissionResult = uiState.studentSubmissionResult

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(ViewStudentSubmissionUiEvent.UserMessageShown)
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                        IconButton(
                            onClick = {
                                onEvent(
                                    ViewStudentSubmissionUiEvent.OnExpandedAppBarDropdownChange(
                                        true,
                                    ),
                                )
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
                                onEvent(
                                    ViewStudentSubmissionUiEvent.OnExpandedAppBarDropdownChange(
                                        false,
                                    ),
                                )
                            },
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(id = R.string.refresh))
                                },
                                onClick = {
                                    onEvent(
                                        ViewStudentSubmissionUiEvent.OnExpandedAppBarDropdownChange(
                                            false,
                                        ),
                                    )
                                    onEvent(ViewStudentSubmissionUiEvent.Refresh)
                                },
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            studentSubmissionResult.data?.let { studentSubmission ->
                GradeBottomBar(
                    studentSubmission = studentSubmission,
                    grade = uiState.grade,
                    onReturnClick = {
                        onEvent(ViewStudentSubmissionUiEvent.OnOpenReturnDialogChange(true))
                    },
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = {
                onEvent(ViewStudentSubmissionUiEvent.Refresh)
            },
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            when (studentSubmissionResult) {
                is Result.Empty -> {}

                is Result.Error -> {
                    ErrorScreen(
                        onRetryClick = {
                            onEvent(ViewStudentSubmissionUiEvent.Retry)
                        },
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = studentSubmissionResult.message!!.asString(),
                    )
                }

                is Result.Loading -> {
                    LoadingScreen()
                }

                is Result.Success -> {
                    val studentSubmission = studentSubmissionResult.data!!
                    ViewStudentSubmissionContent(
                        uiState = uiState,
                        onEvent = onEvent,
                        studentSubmission = studentSubmission,
                        onNavigateToImageViewer = onNavigateToImageViewer,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun ViewStudentSubmissionContent(
    uiState: ViewStudentSubmissionUiState,
    onEvent: (ViewStudentSubmissionUiEvent) -> Unit,
    studentSubmission: StudentSubmission,
    onNavigateToImageViewer: (url: String, title: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val fileUtils = remember { FileUtils(context) }
    val attachments = studentSubmission.assignmentSubmission?.attachments
    val submissionState = studentSubmission.state
    val student = studentSubmission.user
    val studentName = student?.name.orEmpty()
    val courseWork = studentSubmission.courseWork!!
    val maxPoints = courseWork.maxPoints
    val isCourseWorkGraded = maxPoints != null && maxPoints > 0
    val draftGrade =
        uiState.grade.text
            .toString()
            .toDraftGrade()
    val dueDateTime =
        remember {
            courseWork.dueTime?.let { dueTime ->
                Instant.parse(dueTime)
            }
        }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier = modifier,
        contentPadding =
            PaddingValues(
                horizontal = 16.dp,
                vertical = 12.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        content = {
            student?.let {
                header {
                    Row(
                        modifier = Modifier.padding(bottom = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        UserAvatar(
                            id = student.id.orEmpty(),
                            fullName = studentName,
                            photoUrl = student.photoUrl,
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = studentName,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            when {
                                isCourseWorkGraded && studentSubmission.assignedGrade != null -> {
                                    Text(
                                        text = stringResource(id = R.string.graded),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }

                                submissionState == SubmissionState.TURNED_IN -> {
                                    Text(
                                        text = stringResource(id = R.string.turned_in),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }

                                !isCourseWorkGraded && submissionState == SubmissionState.RETURNED -> {
                                    Text(
                                        text = stringResource(id = R.string.returned),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }

                                dueDateTime != null && DateTimeUtils.isPast(dueDateTime) -> {
                                    Text(
                                        text = stringResource(id = R.string.missing),
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }

                                else -> {
                                    Text(
                                        text = stringResource(id = R.string.assigned),
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
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
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(128.dp),
                                errorMessage = stringResource(id = R.string.no_files_attached),
                            )
                        }
                    } else {
                        header {
                            Text(
                                text = stringResource(id = R.string.attachments),
                                modifier =
                                    Modifier.padding(
                                        top = 14.dp,
                                        bottom = 6.dp,
                                    ),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        items(attachments) { attachment ->
                            AttachmentsListItem(
                                material = attachment,
                                fileUtils = fileUtils,
                                onClickFile = { mimeType, url, title ->
                                    if (mimeType == FileType.IMAGE) {
                                        onNavigateToImageViewer(url, title)
                                    } else {
                                        val browserIntent =
                                            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(browserIntent)
                                    }
                                },
                                onClickLink = { url ->
                                    val browserIntent =
                                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(browserIntent)
                                },
                            )
                        }
                    }
                }

                CourseWorkType.SHORT_ANSWER_QUESTION -> {
                    val shortAnswerSubmission =
                        studentSubmission.shortAnswerSubmission

                    header {
                        OutlinedCard(modifier = Modifier.padding(top = 14.dp)) {
                            Box(
                                modifier =
                                    Modifier
                                        .height(56.dp)
                                        .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Text(
                                    text = stringResource(id = R.string.answer),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                            if (shortAnswerSubmission?.answer != null) {
                                Text(
                                    text = shortAnswerSubmission.answer,
                                    modifier =
                                        Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 12.dp,
                                        ),
                                )
                            } else {
                                ErrorScreen(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(128.dp),
                                    errorMessage =
                                        stringResource(
                                            id = R.string.the_student_has_not_answered_the_question_yet,
                                        ),
                                )
                            }
                        }
                    }
                }

                CourseWorkType.MULTIPLE_CHOICE_QUESTION -> {
                    val multipleChoiceSubmission =
                        studentSubmission.multipleChoiceSubmission

                    header {
                        OutlinedCard(modifier = Modifier.padding(top = 14.dp)) {
                            Box(
                                modifier =
                                    Modifier
                                        .height(56.dp)
                                        .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Text(
                                    text = stringResource(id = R.string.answer),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                            courseWork.multipleChoiceQuestion?.let { multipleChoiceQuestion ->
                                Spacer(modifier = Modifier.height(12.dp))
                                multipleChoiceQuestion.choices?.forEach { text ->
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                            .padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        RadioButton(
                                            selected = multipleChoiceSubmission != null && multipleChoiceSubmission.answer == text,
                                            onClick = null, // null recommended for accessibility with screen readers
                                        )
                                        Text(
                                            text = text,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.padding(start = 16.dp),
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                else -> {}
            }
        },
    )

    ReturnDialog(
        open = uiState.openReturnDialog,
        studentName = studentName,
        maxPoints = maxPoints,
        submissionState = studentSubmission.state,
        assignedGrade = studentSubmission.assignedGrade,
        draftGrade = draftGrade,
        onDismissRequest = {
            onEvent(ViewStudentSubmissionUiEvent.OnOpenReturnDialogChange(false))
        },
        onConfirmButtonClick = {
            onEvent(ViewStudentSubmissionUiEvent.Return(studentSubmission.id!!, draftGrade))
        },
    )

    ProgressDialog(
        open = uiState.openProgressDialog,
        onDismissRequest = {},
    )
}

fun String.toDraftGrade(): Int? = trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
