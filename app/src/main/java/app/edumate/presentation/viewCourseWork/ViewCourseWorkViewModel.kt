package app.edumate.presentation.viewCourseWork

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
import app.edumate.core.Supabase
import app.edumate.core.UiText
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.domain.model.material.DriveFile
import app.edumate.domain.model.material.Link
import app.edumate.domain.model.material.Material
import app.edumate.domain.usecase.GetUrlMetadataUseCase
import app.edumate.domain.usecase.authentication.GetCurrentUserUseCase
import app.edumate.domain.usecase.comment.DeleteCommentUseCase
import app.edumate.domain.usecase.comment.UpdateCommentUseCase
import app.edumate.domain.usecase.courseWork.GetCourseWorkUseCase
import app.edumate.domain.usecase.storage.DeleteFileUseCase
import app.edumate.domain.usecase.storage.UploadFileUseCase
import app.edumate.domain.usecase.studentSubmission.CreateSubmissionCommentUseCase
import app.edumate.domain.usecase.studentSubmission.GetStudentSubmissionUseCase
import app.edumate.domain.usecase.studentSubmission.GetSubmissionCommentsUseCase
import app.edumate.domain.usecase.studentSubmission.ModifyStudentSubmissionAttachmentsUseCase
import app.edumate.domain.usecase.studentSubmission.ReclaimStudentSubmissionUseCase
import app.edumate.domain.usecase.studentSubmission.TurnInStudentSubmissionUseCase
import app.edumate.domain.usecase.studentSubmission.UpdateStudentSubmissionUseCase
import app.edumate.domain.usecase.validation.ValidateTextField
import app.edumate.navigation.Screen
import app.edumate.presentation.components.CommentsBottomSheetUiEvent
import app.edumate.presentation.components.CommentsBottomSheetUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File

class ViewCourseWorkViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getCourseWorkUseCase: GetCourseWorkUseCase,
    private val getStudentSubmissionUseCase: GetStudentSubmissionUseCase,
    private val modifyStudentSubmissionAttachmentsUseCase: ModifyStudentSubmissionAttachmentsUseCase,
    private val turnInStudentSubmissionUseCase: TurnInStudentSubmissionUseCase,
    private val reclaimStudentSubmissionUseCase: ReclaimStudentSubmissionUseCase,
    private val updateStudentSubmissionUseCase: UpdateStudentSubmissionUseCase,
    private val getSubmissionCommentsUseCase: GetSubmissionCommentsUseCase,
    private val createSubmissionCommentUseCase: CreateSubmissionCommentUseCase,
    private val updateCommentUseCase: UpdateCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val getUrlMetadataUseCase: GetUrlMetadataUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val deleteFileUseCase: DeleteFileUseCase,
    private val validateTextField: ValidateTextField,
) : ViewModel() {
    var uiState by mutableStateOf(ViewCourseWorkUiState())
        private set
    var commentsUiState by mutableStateOf(CommentsBottomSheetUiState())
        private set

    private val args = savedStateHandle.toRoute<Screen.ViewCourseWork>()
    private val courseId = args.courseId
    private val courseWorkId = args.courseWorkId
    private val isCurrentUserStudent = args.isCurrentUserStudent
    private var getCourseWorkJob: Job? = null
    private var getStudentSubmissionJob: Job? = null
    private var getSubmissionCommentsJob: Job? = null
    private var studentSubmissionId: String? = null
    private var courseWorkType: CourseWorkType? = null

    init {
        getCurrentUser()
        getCourseWork(false)
        getStudentSubmission()
    }

    fun onEvent(event: ViewCourseWorkUiEvent) {
        when (event) {
            is ViewCourseWorkUiEvent.AddLinkAttachment -> {
                getUrlMetadata(event.link)
            }

            is ViewCourseWorkUiEvent.OnEditShortAnswerChange -> {
                uiState = uiState.copy(editShortAnswer = event.edit)
            }

            is ViewCourseWorkUiEvent.OnExpandedAppBarDropdownChange -> {
                uiState = uiState.copy(expandedAppBarDropdown = event.expanded)
            }

            is ViewCourseWorkUiEvent.OnFilePicked -> {
                uploadFile(
                    title = event.title,
                    file = event.file,
                    mimeType = event.mimeType,
                    size = event.size,
                )
            }

            is ViewCourseWorkUiEvent.OnMultipleChoiceAnswerValueChange -> {
                uiState = uiState.copy(multipleChoiceAnswer = event.answer)
            }

            is ViewCourseWorkUiEvent.OnOpenAddLinkDialogChange -> {
                uiState = uiState.copy(openAddLinkDialog = event.open)
            }

            is ViewCourseWorkUiEvent.OnOpenRemoveAttachmentDialogChange -> {
                uiState = uiState.copy(removeAttachmentIndex = event.index)
            }

            is ViewCourseWorkUiEvent.OnOpenTurnInDialogChange -> {
                uiState = uiState.copy(openTurnInDialog = event.open)
            }

            is ViewCourseWorkUiEvent.OnOpenUnSubmitDialogChange -> {
                uiState = uiState.copy(openUnSubmitDialog = event.open)
            }

            is ViewCourseWorkUiEvent.OnShowAddAttachmentBottomSheetChange -> {
                uiState = uiState.copy(showAddAttachmentBottomSheet = event.show)
            }

            is ViewCourseWorkUiEvent.OnShowCommentsBottomSheetChange -> {
                uiState = uiState.copy(showCommentsBottomSheet = event.show)
            }

            is ViewCourseWorkUiEvent.OnShowStudentSubmissionBottomSheetChange -> {
                uiState = uiState.copy(showStudentSubmissionBottomSheet = event.show)
            }

            ViewCourseWorkUiEvent.Reclaim -> {
                reclaim()
            }

            ViewCourseWorkUiEvent.Refresh -> {
                getCourseWork(uiState.courseWorkResult is Result.Success)
                getStudentSubmission()
            }

            is ViewCourseWorkUiEvent.RemoveAttachment -> {
                val attachment = uiState.assignmentAttachments[event.position]
                when {
                    attachment.driveFile != null -> {
                        deleteFile(attachment)
                    }

                    attachment.link != null -> {
                        uiState.assignmentAttachments.removeAt(event.position)
                        modifyAttachments(uiState.assignmentAttachments)
                    }
                }
            }

            ViewCourseWorkUiEvent.Retry -> {
                getCourseWork(false)
            }

            ViewCourseWorkUiEvent.RetryStudentSubmission -> {
                getStudentSubmission()
            }

            is ViewCourseWorkUiEvent.TurnIn -> {
                when (courseWorkType) {
                    CourseWorkType.ASSIGNMENT -> {
                        turnIn()
                    }

                    CourseWorkType.MULTIPLE_CHOICE_QUESTION -> {
                        updateStudentSubmission(
                            multipleChoiceAnswer = uiState.multipleChoiceAnswer,
                            shortAnswer = null,
                        )
                    }

                    CourseWorkType.SHORT_ANSWER_QUESTION -> {
                        updateStudentSubmission(
                            multipleChoiceAnswer = null,
                            shortAnswer =
                                uiState.shortAnswer.text
                                    .toString()
                                    .trim(),
                        )
                    }

                    else -> {}
                }
            }

            ViewCourseWorkUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    fun onEvent(event: CommentsBottomSheetUiEvent) {
        when (event) {
            is CommentsBottomSheetUiEvent.AddComment -> {
                studentSubmissionId?.let {
                    if (commentsUiState.editCommentId != null) {
                        updateComment(
                            commentId = commentsUiState.editCommentId!!,
                            text = event.text,
                        )
                    } else {
                        addComment(event.text)
                    }
                }
            }

            is CommentsBottomSheetUiEvent.DeleteComment -> {
                studentSubmissionId?.let {
                    deleteComment(event.commentId)
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
                studentSubmissionId?.let {
                    getComments(false)
                }
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

    private fun getCourseWork(isRefreshing: Boolean) {
        // Cancel any ongoing getCourseWorkJob before making a new call.
        getCourseWorkJob?.cancel()
        getCourseWorkJob =
            getCourseWorkUseCase(courseWorkId)
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
                                    uiState.copy(courseWorkResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with isRefreshing = true.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(isRefreshing = true)
                                } else {
                                    uiState.copy(courseWorkResult = result)
                                }
                        }

                        is Result.Success -> {
                            val courseWork = result.data!!
                            courseWorkType = courseWork.workType

                            uiState =
                                uiState.copy(
                                    courseWork = courseWork,
                                    courseWorkResult = result,
                                    isRefreshing = false,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
    }

    private fun getStudentSubmission() {
        if (!isCurrentUserStudent || courseWorkType == CourseWorkType.MATERIAL) return

        // Cancel any ongoing getStudentSubmissionJob before making a new call.
        getStudentSubmissionJob?.cancel()
        getStudentSubmissionJob =
            getStudentSubmissionUseCase(courseId, courseWorkId)
                .onEach { result ->
                    if (result is Result.Success) {
                        val studentSubmission = result.data!!
                        studentSubmissionId = studentSubmission.id

                        when (courseWorkType) {
                            CourseWorkType.ASSIGNMENT -> {
                                val attachments =
                                    studentSubmission.assignmentSubmission?.attachments.orEmpty()
                                uiState.assignmentAttachments.clear()
                                uiState.assignmentAttachments.addAll(attachments)
                            }

                            CourseWorkType.MULTIPLE_CHOICE_QUESTION -> {
                                val answer =
                                    studentSubmission.multipleChoiceSubmission?.answer.orEmpty()
                                uiState = uiState.copy(multipleChoiceAnswer = answer)
                            }

                            CourseWorkType.SHORT_ANSWER_QUESTION -> {
                                val answer =
                                    studentSubmission.shortAnswerSubmission?.answer.orEmpty()
                                uiState.shortAnswer.setTextAndPlaceCursorAtEnd(answer)
                            }

                            else -> {}
                        }

                        studentSubmissionId?.let { getComments(false) }
                    }

                    uiState = uiState.copy(studentSubmissionResult = result)
                }.launchIn(viewModelScope)
    }

    private fun modifyAttachments(attachments: List<Material>) {
        studentSubmissionId ?: return

        modifyStudentSubmissionAttachmentsUseCase(studentSubmissionId!!, attachments)
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
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun updateStudentSubmission(
        multipleChoiceAnswer: String?,
        shortAnswer: String?,
    ) {
        studentSubmissionId ?: return

        updateStudentSubmissionUseCase(studentSubmissionId!!, multipleChoiceAnswer, shortAnswer)
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
                        uiState = uiState.copy(editShortAnswer = false)
                        turnIn()
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun turnIn() {
        studentSubmissionId ?: return

        turnInStudentSubmissionUseCase(courseWorkId, studentSubmissionId!!)
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
                        getStudentSubmission()
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun reclaim() {
        studentSubmissionId ?: return

        reclaimStudentSubmissionUseCase(studentSubmissionId!!)
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
                        getStudentSubmission()
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun addComment(text: String) {
        studentSubmissionId ?: return

        val textResult = validateTextField.execute(text)
        if (!textResult.successful) {
            commentsUiState =
                commentsUiState.copy(
                    userMessage = UiText.StringResource(R.string.missing_message),
                )
            return
        }

        createSubmissionCommentUseCase(courseId, studentSubmissionId!!, text)
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
        studentSubmissionId ?: return

        // Cancel any ongoing getSubmissionCommentsJob before making a new call.
        getSubmissionCommentsJob?.cancel()
        getSubmissionCommentsJob =
            getSubmissionCommentsUseCase(studentSubmissionId!!)
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

    private fun uploadFile(
        title: String,
        file: File,
        mimeType: String?,
        size: Long?,
    ) {
        studentSubmissionId ?: return

        uploadFileUseCase(
            bucketId = Supabase.Storage.MATERIALS_BUCKET_ID,
            path = "$courseId/coursework/$courseWorkId/submission/$studentSubmissionId/$title",
            file = file,
        ).onEach { result ->
            when (result) {
                is Result.Empty -> {}

                is Result.Error -> {
                    uiState =
                        uiState.copy(
                            uploadProgress = null,
                            userMessage = result.message,
                        )
                }

                is Result.Loading -> {
                    uiState = uiState.copy(uploadProgress = 0.0f)
                }

                is Result.Success -> {
                    val state = result.data!!
                    if (state.isDone) {
                        val driveFile =
                            DriveFile(
                                alternateLink = state.url,
                                mimeType = mimeType,
                                size = size,
                                title = title,
                            )
                        uiState.assignmentAttachments.add(Material(driveFile = driveFile))
                        uiState = uiState.copy(uploadProgress = null)
                        modifyAttachments(uiState.assignmentAttachments)
                    } else {
                        uiState = uiState.copy(uploadProgress = state.progress)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteFile(material: Material) {
        studentSubmissionId ?: return

        val title = material.driveFile?.title ?: return

        deleteFileUseCase(
            bucketId = Supabase.Storage.MATERIALS_BUCKET_ID,
            paths = listOf("$courseId/coursework/$courseWorkId/submission/$studentSubmissionId/$title"),
        ).onEach { result ->
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
                    uiState.assignmentAttachments.remove(material)
                    modifyAttachments(uiState.assignmentAttachments)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getUrlMetadata(url: String) {
        studentSubmissionId ?: return

        getUrlMetadataUseCase(url)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        val link =
                            Link(
                                url = url,
                                title = url,
                            )
                        uiState.assignmentAttachments.add(Material(link = link))
                        uiState = uiState.copy(openProgressDialog = false)
                    }

                    is Result.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }

                    is Result.Success -> {
                        val link = result.data!!
                        uiState.assignmentAttachments.add(Material(link = link))
                        modifyAttachments(uiState.assignmentAttachments)
                    }
                }
            }.launchIn(viewModelScope)
    }
}
