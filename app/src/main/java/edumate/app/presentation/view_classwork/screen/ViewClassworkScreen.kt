package edumate.app.presentation.view_classwork.screen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import edumate.app.R.string as Strings
import edumate.app.core.DataState
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.components.ComingSoon
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.view_classwork.ViewClassworkUiEvent
import edumate.app.presentation.view_classwork.ViewClassworkUiState
import edumate.app.presentation.view_classwork.screen.components.ContentAssignment
import edumate.app.presentation.view_classwork.screen.components.ContentMaterial
import edumate.app.presentation.view_classwork.screen.components.TurnInDialog
import edumate.app.presentation.view_classwork.screen.components.UnSubmitDialog

@OptIn(ExperimentalMaterial3Api::class)
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
        assignedStudent: UserProfile
    ) -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current

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
                if (currentUserType == UserType.TEACHER) {
                    IconButton(
                        onClick = { share(context, uiState.classwork.alternateLink) }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null
                        )
                    }
                }
                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
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
                when (classworkType) {
                    CourseWorkType.MATERIAL -> {
                        ContentMaterial(
                            uiState = uiState,
                            onEvent = onEvent
                        )
                    }

                    CourseWorkType.ASSIGNMENT -> {
                        ContentAssignment(
                            uiState = uiState,
                            onEvent = onEvent,
                            snackbarHostState = snackbarHostState,
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
            }

            DataState.UNKNOWN -> {}
        }
    }

    TurnInDialog(
        onDismissRequest = {
            onEvent(ViewClassworkUiEvent.OnOpenTurnInDialog(false))
        },
        uiState = uiState,
        onConfirmClick = {
            onEvent(ViewClassworkUiEvent.TurnIn)
        }
    )

    UnSubmitDialog(
        onDismissRequest = {
            onEvent(ViewClassworkUiEvent.OnOpenUnSubmitDialog(false))
        },
        uiState = uiState,
        onConfirmClick = {
            onEvent(ViewClassworkUiEvent.UnSubmit)
        }
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
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