package app.edumate.presentation.viewCourseWork

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.edumate.core.Result
import app.edumate.core.Supabase
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.domain.model.material.DriveFile
import app.edumate.domain.model.material.Material
import app.edumate.domain.usecase.courseWork.GetCourseWorkUseCase
import app.edumate.domain.usecase.storage.DeleteFileUseCase
import app.edumate.domain.usecase.storage.UploadFileUseCase
import app.edumate.domain.usecase.studentSubmission.GetStudentSubmissionUseCase
import app.edumate.domain.usecase.studentSubmission.ModifyStudentSubmissionAttachmentsUseCase
import app.edumate.domain.usecase.studentSubmission.ReclaimStudentSubmissionUseCase
import app.edumate.domain.usecase.studentSubmission.TurnInStudentSubmissionUseCase
import app.edumate.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File

class ViewCourseWorkViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCourseWorkUseCase: GetCourseWorkUseCase,
    private val getStudentSubmissionUseCase: GetStudentSubmissionUseCase,
    private val modifyStudentSubmissionAttachmentsUseCase: ModifyStudentSubmissionAttachmentsUseCase,
    private val turnInStudentSubmissionUseCase: TurnInStudentSubmissionUseCase,
    private val reclaimStudentSubmissionUseCase: ReclaimStudentSubmissionUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val deleteFileUseCase: DeleteFileUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(ViewCourseWorkUiState())
        private set

    private val args = savedStateHandle.toRoute<Screen.ViewCourseWork>()
    private val courseId = args.courseId
    private val courseWorkId = args.id
    private val isCurrentUserStudent = args.isCurrentUserStudent
    private var getCourseWorkJob: Job? = null
    private var getStudentSubmissionJob: Job? = null
    private var studentSubmissionId: String? = null
    private var courseWorkType: CourseWorkType? = null

    init {
        getCourseWork(
            id = courseWorkId,
            isRefreshing = false,
        )
    }

    fun onEvent(event: ViewCourseWorkUiEvent) {
        when (event) {
            is ViewCourseWorkUiEvent.OnEditShortAnswerChange -> {
                uiState = uiState.copy(editShortAnswer = event.edit)
            }

            is ViewCourseWorkUiEvent.OnExpandedAppBarDropdownChange -> {
                uiState = uiState.copy(expandedAppBarDropdown = event.expanded)
            }

            is ViewCourseWorkUiEvent.OnFilePicked -> {
                uploadFile(
                    courseId = courseId,
                    courseWorkId = courseWorkId,
                    submissionId = studentSubmissionId!!,
                    title = event.title,
                    file = event.file,
                    mimeType = event.mimeType,
                    size = event.size,
                )
            }

            is ViewCourseWorkUiEvent.OnMultipleChoiceAnswerValueChange -> {
                uiState = uiState.copy(multipleChoiceAnswer = event.answer)
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

            is ViewCourseWorkUiEvent.OnShowStudentSubmissionBottomSheetChange -> {
                uiState = uiState.copy(showStudentSubmissionBottomSheet = event.show)
            }

            ViewCourseWorkUiEvent.Reclaim -> {
                reclaim(studentSubmissionId!!)
            }

            ViewCourseWorkUiEvent.Refresh -> {
                getCourseWork(id = courseWorkId, isRefreshing = true)
            }

            is ViewCourseWorkUiEvent.RemoveAttachment -> {
                val attachment = uiState.assignmentAttachments[event.position]
                deleteFile(
                    courseId = courseId,
                    courseWorkId = courseWorkId,
                    submissionId = studentSubmissionId!!,
                    material = attachment,
                )
            }

            ViewCourseWorkUiEvent.Retry -> {
                getCourseWork(id = courseWorkId, isRefreshing = false)
            }

            ViewCourseWorkUiEvent.RetryStudentSubmission -> {
                getStudentSubmission(
                    courseId = courseId,
                    courseWorkId = courseWorkId,
                    courseWorkType = courseWorkType!!,
                )
            }

            is ViewCourseWorkUiEvent.TurnIn -> {
                turnIn(courseWorkId = courseWorkId, id = studentSubmissionId!!)
            }

            ViewCourseWorkUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun getCourseWork(
        id: String,
        isRefreshing: Boolean,
    ) {
        // Cancel any ongoing getCourseWorkJob before making a new call.
        getCourseWorkJob?.cancel()
        getCourseWorkJob =
            getCourseWorkUseCase(id)
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
                            uiState =
                                uiState.copy(
                                    courseWorkResult = result,
                                    isRefreshing = false,
                                )

                            val courseWork = result.data!!
                            courseWorkType = courseWork.workType

                            if (isCurrentUserStudent && courseWorkType != CourseWorkType.MATERIAL) {
                                getStudentSubmission(
                                    courseId = courseId,
                                    courseWorkId = courseWorkId,
                                    courseWorkType = courseWorkType!!,
                                )
                            }
                        }
                    }
                }.launchIn(viewModelScope)
    }

    private fun getStudentSubmission(
        courseId: String,
        courseWorkId: String,
        courseWorkType: CourseWorkType,
    ) {
        // Cancel any ongoing getStudentSubmissionJob before making a new call.
        getStudentSubmissionJob?.cancel()
        getStudentSubmissionJob =
            getStudentSubmissionUseCase(courseId, courseWorkId, courseWorkType)
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
                    }

                    uiState = uiState.copy(studentSubmissionResult = result)
                }.launchIn(viewModelScope)
    }

    private fun modifyAttachments(
        id: String,
        attachments: List<Material>,
    ) {
        modifyStudentSubmissionAttachmentsUseCase(id, attachments)
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

    private fun turnIn(
        courseWorkId: String,
        id: String,
    ) {
        turnInStudentSubmissionUseCase(courseWorkId, id)
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
                        getStudentSubmission(courseId, courseWorkId, courseWorkType!!)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun reclaim(id: String) {
        reclaimStudentSubmissionUseCase(id)
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
                        getStudentSubmission(courseId, courseWorkId, courseWorkType!!)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun uploadFile(
        courseId: String,
        courseWorkId: String,
        submissionId: String,
        title: String,
        file: File,
        mimeType: String?,
        size: Long?,
    ) {
        uploadFileUseCase(
            bucketId = Supabase.Storage.MATERIALS_BUCKET_ID,
            path = "$courseId/coursework/$courseWorkId/submission/$submissionId/$title",
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
                        modifyAttachments(submissionId, uiState.assignmentAttachments)
                    } else {
                        uiState = uiState.copy(uploadProgress = state.progress)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteFile(
        courseId: String,
        courseWorkId: String,
        submissionId: String,
        material: Material,
    ) {
        val title = material.driveFile?.title ?: return

        deleteFileUseCase(
            bucketId = Supabase.Storage.MATERIALS_BUCKET_ID,
            paths = listOf("$courseId/coursework/$courseWorkId/submission/$submissionId/$title"),
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
                    modifyAttachments(submissionId, uiState.assignmentAttachments)
                }
            }
        }.launchIn(viewModelScope)
    }
}
