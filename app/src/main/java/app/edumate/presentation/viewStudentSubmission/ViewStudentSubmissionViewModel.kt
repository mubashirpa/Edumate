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
    private var submissionId: String? = null
    private var getStudentSubmissionJob: Job? = null
    private var getSubmissionCommentsJob: Job? = null

    init {
        getStudentSubmission(
            isRefreshing = false,
            courseId = args.courseId,
            courseWorkId = args.courseWorkId,
            studentId = args.studentId,
        )
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
                if (commentsUiState.commentsResult is Result.Empty) {
                    submissionId?.let {
                        getComments(it, false)
                    }
                }
                uiState = uiState.copy(showCommentsBottomSheet = event.show)
            }

            ViewStudentSubmissionUiEvent.Refresh -> {
                getStudentSubmission(
                    isRefreshing = uiState.studentSubmissionResult is Result.Success,
                    courseId = args.courseId,
                    courseWorkId = args.courseWorkId,
                    studentId = args.studentId,
                )
            }

            ViewStudentSubmissionUiEvent.Retry -> {
                getStudentSubmission(
                    isRefreshing = false,
                    courseId = args.courseId,
                    courseWorkId = args.courseWorkId,
                    studentId = args.studentId,
                )
            }

            is ViewStudentSubmissionUiEvent.Return -> {
                event.grade?.let { grade ->
                    updateStudentSubmission(
                        id = event.id,
                        grade = grade,
                    )
                } ?: returnStudentSubmission(event.id)
            }

            ViewStudentSubmissionUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    fun onEvent(event: CommentsBottomSheetUiEvent) {
        when (event) {
            is CommentsBottomSheetUiEvent.AddComment -> {
                submissionId?.let {
                    if (commentsUiState.editCommentId != null) {
                        updateComment(
                            submissionId = it,
                            id = commentsUiState.editCommentId!!,
                            text = event.text,
                        )
                    } else {
                        addComment(
                            courseId = args.courseId,
                            submissionId = it,
                            text = event.text,
                        )
                    }
                }
            }

            is CommentsBottomSheetUiEvent.DeleteComment -> {
                submissionId?.let {
                    deleteComment(
                        submissionId = it,
                        id = event.commentId,
                    )
                }
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
                submissionId?.let {
                    getComments(it, false)
                }
            }

            CommentsBottomSheetUiEvent.UserMessageShown -> {
                commentsUiState = commentsUiState.copy(userMessage = null)
            }
        }
    }

    private fun getStudentSubmission(
        isRefreshing: Boolean,
        courseId: String,
        courseWorkId: String,
        studentId: String,
    ) {
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
                                )
                        }
                    }
                }.launchIn(viewModelScope)
    }

    private fun returnStudentSubmission(id: String) {
        returnStudentSubmissionUseCase(id)
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
                        getStudentSubmission(
                            isRefreshing = true,
                            courseId = args.courseId,
                            courseWorkId = args.courseWorkId,
                            studentId = args.studentId,
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun updateStudentSubmission(
        id: String,
        grade: Int?,
    ) {
        updateStudentSubmissionUseCase(id = id, assignedGrade = grade)
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
                        returnStudentSubmission(id)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun addComment(
        courseId: String,
        submissionId: String,
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

        createSubmissionCommentUseCase(courseId, submissionId, text)
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
                        getComments(submissionId, true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getComments(
        submissionId: String,
        isRefreshing: Boolean,
    ) {
        // Cancel any ongoing getSubmissionCommentsJob before making a new call.
        getSubmissionCommentsJob?.cancel()
        getSubmissionCommentsJob =
            getSubmissionCommentsUseCase(submissionId)
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
                            commentsUiState =
                                commentsUiState.copy(
                                    isLoading = false,
                                    commentsResult = result,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
    }

    private fun updateComment(
        submissionId: String,
        id: String,
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

        updateCommentUseCase(id, text)
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
                        getComments(submissionId, true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun deleteComment(
        submissionId: String,
        id: String,
    ) {
        deleteCommentUseCase(id)
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
                        getComments(submissionId, true)
                    }
                }
            }.launchIn(viewModelScope)
    }
}
