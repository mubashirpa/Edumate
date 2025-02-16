package app.edumate.presentation.viewStudentSubmission

import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.usecase.authentication.GetCurrentUserUseCase
import app.edumate.domain.usecase.comment.DeleteCommentUseCase
import app.edumate.domain.usecase.comment.UpdateCommentUseCase
import app.edumate.domain.usecase.studentSubmission.CreateSubmissionCommentUseCase
import app.edumate.domain.usecase.studentSubmission.GetStudentSubmissionUseCase
import app.edumate.domain.usecase.studentSubmission.GetSubmissionCommentsUseCase
import app.edumate.domain.usecase.studentSubmission.ReturnStudentSubmissionUseCase
import app.edumate.domain.usecase.studentSubmission.UpdateStudentSubmissionUseCase
import app.edumate.domain.usecase.validation.ValidateTextField
import app.edumate.navigation.Screen
import app.edumate.presentation.components.CommentsBottomSheetUiEvent
import app.edumate.presentation.components.CommentsBottomSheetUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ViewStudentSubmissionViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getStudentSubmissionUseCase: GetStudentSubmissionUseCase,
    private val returnStudentSubmissionUseCase: ReturnStudentSubmissionUseCase,
    private val updateStudentSubmissionUseCase: UpdateStudentSubmissionUseCase,
    private val getSubmissionCommentsUseCase: GetSubmissionCommentsUseCase,
    private val createSubmissionCommentUseCase: CreateSubmissionCommentUseCase,
    private val updateCommentUseCase: UpdateCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val validateTextField: ValidateTextField,
) : ViewModel() {
    var uiState by mutableStateOf(ViewStudentSubmissionUiState())
        private set
    var commentsUiState by mutableStateOf(CommentsBottomSheetUiState())
        private set

    private val args = savedStateHandle.toRoute<Screen.ViewStudentSubmission>()
    private val courseId = args.courseId
    private val courseWorkId = args.courseWorkId
    private val studentId = args.studentId
    private var getStudentSubmissionJob: Job? = null
    private var getSubmissionCommentsJob: Job? = null
    private var submissionId: String? = null

    init {
        getCurrentUser()
        getStudentSubmission(false)
    }

    fun onEvent(event: ViewStudentSubmissionUiEvent) {
        when (event) {
            is ViewStudentSubmissionUiEvent.OnExpandedAppBarDropdownChange -> {
                uiState = uiState.copy(expandedAppBarDropdown = event.expanded)
            }

            is ViewStudentSubmissionUiEvent.OnOpenReturnDialogChange -> {
                uiState = uiState.copy(openReturnDialog = event.open)
            }

            is ViewStudentSubmissionUiEvent.OnShowCommentsBottomSheetChange -> {
                uiState = uiState.copy(showCommentsBottomSheet = event.show)
            }

            ViewStudentSubmissionUiEvent.Refresh -> {
                getStudentSubmission(uiState.studentSubmissionResult is Result.Success)
            }

            ViewStudentSubmissionUiEvent.Retry -> {
                getStudentSubmission(false)
            }

            is ViewStudentSubmissionUiEvent.Return -> {
                event.grade?.let { grade ->
                    updateStudentSubmission(grade)
                } ?: returnStudentSubmission()
            }

            ViewStudentSubmissionUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    fun onEvent(event: CommentsBottomSheetUiEvent) {
        when (event) {
            is CommentsBottomSheetUiEvent.AddComment -> {
                if (commentsUiState.editCommentId != null) {
                    updateComment(
                        commentId = commentsUiState.editCommentId!!,
                        text = event.text,
                    )
                } else {
                    addComment(event.text)
                }
            }

            is CommentsBottomSheetUiEvent.DeleteComment -> {
                deleteComment(event.commentId)
            }

            is CommentsBottomSheetUiEvent.OnEditComment -> {
                commentsUiState =
                    commentsUiState.copy(editCommentId = event.commentId)
                commentsUiState.comment.setTextAndPlaceCursorAtEnd(event.text)
            }

            is CommentsBottomSheetUiEvent.OnOpenDeleteCommentDialogChange -> {
                commentsUiState =
                    commentsUiState.copy(deleteCommentId = event.commentId)
            }

            CommentsBottomSheetUiEvent.Retry -> {
                getComments(false)
            }

            CommentsBottomSheetUiEvent.UserMessageShown -> {
                commentsUiState = commentsUiState.copy(userMessage = null)
            }
        }
    }

    private fun getCurrentUser() {
        getCurrentUserUseCase()
            .onEach { result ->
                if (result is Result.Success) {
                    result.data?.id?.let { userId ->
                        uiState = uiState.copy(currentUserId = userId)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getStudentSubmission(isRefreshing: Boolean) {
        // Cancel any ongoing getStudentSubmissionJob before making a new call.
        getStudentSubmissionJob?.cancel()
        getStudentSubmissionJob =
            getStudentSubmissionUseCase(courseId, courseWorkId, studentId)
                .onEach { result ->
                    when (result) {
                        is Result.Empty -> {}

                        is Result.Error -> {
                            // The Result.Error state is only used during initial loading and retry attempts.
                            // Otherwise, a snackbar is displayed using the userMessage property.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(
                                        isRefreshing = false,
                                        userMessage = result.message,
                                    )
                                } else {
                                    uiState.copy(studentSubmissionResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with isRefreshing = true.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(isRefreshing = true)
                                } else {
                                    uiState.copy(studentSubmissionResult = result)
                                }
                        }

                        is Result.Success -> {
                            val submission = result.data!!
                            submissionId = submission.id
                            submission.assignedGrade?.let { grade ->
                                uiState.grade.setTextAndPlaceCursorAtEnd(grade.toString())
                            }

                            uiState =
                                uiState.copy(
                                    isRefreshing = false,
                                    studentSubmissionResult = result,
                                    title = submission.courseWork?.title.orEmpty(),
                                )

                            getComments(false)
                        }
                    }
                }.launchIn(viewModelScope)
    }

    private fun returnStudentSubmission() {
        submissionId ?: return

        returnStudentSubmissionUseCase(submissionId!!)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }

                    is Result.Success -> {
                        uiState = uiState.copy(openProgressDialog = false)
                        getStudentSubmission(true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun updateStudentSubmission(grade: Int?) {
        submissionId ?: return

        updateStudentSubmissionUseCase(submissionId!!, assignedGrade = grade)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }

                    is Result.Success -> {
                        returnStudentSubmission()
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun addComment(text: String) {
        submissionId ?: return

        val textResult = validateTextField.execute(text)
        if (!textResult.successful) {
            commentsUiState =
                commentsUiState.copy(
                    userMessage = UiText.StringResource(R.string.missing_message),
                )
            return
        }

        createSubmissionCommentUseCase(courseId, submissionId!!, text)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        commentsUiState =
                            commentsUiState.copy(
                                isLoading = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        commentsUiState =
                            commentsUiState.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        commentsUiState.comment.clearText()
                        getComments(true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getComments(isRefreshing: Boolean) {
        submissionId ?: return

        // Cancel any ongoing getSubmissionCommentsJob before making a new call.
        getSubmissionCommentsJob?.cancel()
        getSubmissionCommentsJob =
            getSubmissionCommentsUseCase(submissionId!!)
                .onEach { result ->
                    when (result) {
                        is Result.Empty -> {}

                        is Result.Error -> {
                            // The Result.Error state is only used during initial loading and retry attempts.
                            // Otherwise, a snackbar is displayed using the userMessage property.
                            commentsUiState =
                                if (isRefreshing) {
                                    commentsUiState.copy(
                                        isLoading = false,
                                        userMessage = result.message,
                                    )
                                } else {
                                    commentsUiState.copy(commentsResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with isRefreshing = true.
                            commentsUiState =
                                if (isRefreshing) {
                                    commentsUiState.copy(isLoading = true)
                                } else {
                                    commentsUiState.copy(commentsResult = result)
                                }
                        }

                        is Result.Success -> {
                            val comments = result.data!!

                            commentsUiState =
                                commentsUiState.copy(
                                    isLoading = false,
                                    commentsResult = result,
                                )
                            uiState = uiState.copy(commentsCount = comments.size)
                        }
                    }
                }.launchIn(viewModelScope)
    }

    private fun updateComment(
        commentId: String,
        text: String,
    ) {
        val textResult = validateTextField.execute(text)
        if (!textResult.successful) {
            commentsUiState =
                commentsUiState.copy(
                    userMessage = UiText.StringResource(R.string.missing_message),
                )
            return
        }

        updateCommentUseCase(commentId, text)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        commentsUiState =
                            commentsUiState.copy(
                                isLoading = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        commentsUiState =
                            commentsUiState.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        commentsUiState.comment.clearText()
                        commentsUiState =
                            commentsUiState.copy(editCommentId = null)
                        getComments(true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun deleteComment(commentId: String) {
        deleteCommentUseCase(commentId)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        commentsUiState =
                            commentsUiState.copy(
                                isLoading = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        commentsUiState =
                            commentsUiState.copy(
                                deleteCommentId = null,
                                isLoading = true,
                            )
                    }

                    is Result.Success -> {
                        getComments(true)
                    }
                }
            }.launchIn(viewModelScope)
    }
}
