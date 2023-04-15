package edumate.app.presentation.view_classwork

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.R.string as Strings
import edumate.app.core.DataState
import edumate.app.core.FirebaseConstants
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.domain.model.student_submission.AssignmentSubmission
import edumate.app.domain.model.student_submission.Attachment
import edumate.app.domain.model.student_submission.DriveFile
import edumate.app.domain.model.student_submission.StudentSubmission
import edumate.app.domain.model.student_submission.SubmissionState
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.course_work.GetCourseWorkUseCase
import edumate.app.domain.usecase.storage.DeleteFileUseCase
import edumate.app.domain.usecase.storage.UploadFileUseCase
import edumate.app.domain.usecase.student_submission.GetStudentSubmissionUseCase
import edumate.app.domain.usecase.student_submission.TurnInStudentSubmissionUseCase
import edumate.app.navigation.Routes
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class ViewClassworkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getCourseWorkUseCase: GetCourseWorkUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val deleteFileUseCase: DeleteFileUseCase,
    private val turnInStudentSubmissionUseCase: TurnInStudentSubmissionUseCase,
    private val getStudentSubmissionUseCase: GetStudentSubmissionUseCase
) : ViewModel() {

    var uiState by mutableStateOf(ViewClassworkUiState())
        private set

    private val courseWorkId: String? = try {
        checkNotNull(savedStateHandle[Routes.Args.VIEW_CLASSWORK_WORK_ID])
    } catch (e: IllegalStateException) {
        null
    }
    private val courseId: String? = try {
        checkNotNull(savedStateHandle[Routes.Args.VIEW_CLASSWORK_COURSE_ID])
    } catch (e: IllegalStateException) {
        null
    }
    private var currentUser: FirebaseUser? = null
    private val studentSubmission = mutableStateOf(StudentSubmission())

    init {
        getCurrentUserUseCase().map { user ->
            currentUser = user
            fetchStudentSubmission()
        }.launchIn(viewModelScope)
        fetchClasswork()
    }

    fun onEvent(event: ViewClassworkUiEvent) {
        when (event) {
            is ViewClassworkUiEvent.OnFilePicked -> {
                uploadFile(event.uri, event.fileUtils)
            }

            is ViewClassworkUiEvent.OnOpenYourWorkBottomSheet -> {
                uiState = uiState.copy(openYourWorkBottomSheet = event.open)
            }

            is ViewClassworkUiEvent.OnRemoveAttachment -> {
                deleteFile(event.index)
            }

            ViewClassworkUiEvent.TurnIn -> {
                submitAssignment()
            }

            ViewClassworkUiEvent.UnSubmit -> {
                unSubmitClasswork()
            }
        }
    }

    private fun fetchClasswork() {
        if (courseWorkId != null && courseId != null) {
            getCourseWorkUseCase(courseWorkId, courseId).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        uiState = uiState.copy(dataState = DataState.LOADING)
                    }

                    is Resource.Success -> {
                        val classwork = resource.data
                        uiState = if (classwork != null) {
                            uiState.copy(
                                classwork = classwork,
                                dataState = DataState.SUCCESS
                            )
                        } else {
                            uiState.copy(
                                dataState = DataState.ERROR(
                                    UiText.StringResource(
                                        Strings.cannot_retrieve_classwork_at_this_time_lease_try_again_later
                                    )
                                )
                            )
                        }
                    }

                    is Resource.Error -> {
                        uiState = uiState.copy(dataState = DataState.ERROR(resource.message!!))
                    }
                }
            }.launchIn(viewModelScope)
        } else {
            uiState =
                uiState.copy(
                    dataState = DataState.ERROR(
                        UiText.StringResource(
                            Strings.cannot_retrieve_classwork_at_this_time_lease_try_again_later
                        )
                    )
                )
        }
    }

    private fun fetchStudentSubmission() {
        if (courseWorkId != null && courseId != null && currentUser != null) {
            getStudentSubmissionUseCase(
                courseId,
                courseWorkId,
                currentUser?.uid.orEmpty()
            ).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        uiState = uiState.copy(yourWorkDataState = DataState.LOADING)
                    }

                    is Resource.Success -> {
                        val submission = resource.data
                        val attachments = submission?.assignmentSubmission?.attachments.orEmpty()
                        if (submission != null) {
                            studentSubmission.value = submission
                        }
                        uiState.studentSubmissionAttachments.clear()
                        uiState.studentSubmissionAttachments.addAll(attachments)
                        uiState = uiState.copy(
                            studentSubmissionLate = studentSubmission.value.late,
                            studentSubmissionPoint = studentSubmission.value.assignedGrade,
                            studentSubmissionState = studentSubmission.value.state,
                            yourWorkDataState = DataState.SUCCESS
                        )
                    }

                    is Resource.Error -> {
                        uiState =
                            uiState.copy(yourWorkDataState = DataState.ERROR(resource.message!!))
                    }
                }
            }.launchIn(viewModelScope)
        } else {
            uiState =
                uiState.copy(
                    yourWorkDataState = DataState.ERROR(
                        UiText.StringResource(Strings.error_unexpected)
                    )
                )
        }
    }

    private fun uploadFile(uri: Uri, fileUtils: FileUtils) {
        val fileExtension = fileUtils.getFileExtension(uri)
        val fileName = fileUtils.getFileName(uri) ?: "${uri.lastPathSegment}.$fileExtension"
        val mimeType = fileUtils.getMimeType(uri)

        val filePath =
            "${FirebaseConstants.Storage.COURSE_STORAGE_PATH}/$courseId/course_work/$courseWorkId/$fileName"

        uploadFileUseCase(uri, filePath).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    val fileUrl = resource.data
                    uiState = if (fileUrl != null) {
                        val driveFile = DriveFile(
                            url = fileUrl.toString(),
                            title = fileName,
                            type = mimeType
                        )
                        uiState.studentSubmissionAttachments.add(Attachment(driveFile))
                        uiState.copy(openProgressDialog = false)
                    } else {
                        uiState.copy(
                            openProgressDialog = false,
                            userMessage = UiText.StringResource(Strings.error_unexpected)
                        )
                    }
                }

                is Resource.Error -> {
                    uiState = uiState.copy(
                        openProgressDialog = false,
                        userMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteFile(index: Int) {
        val fileName = uiState.studentSubmissionAttachments[index].driveFile?.title
        val filePath =
            "${FirebaseConstants.Storage.COURSE_STORAGE_PATH}/$courseId/course_work/$courseWorkId/$fileName"

        deleteFileUseCase(filePath).onEach { resource ->
            uiState = when (resource) {
                is Resource.Loading -> {
                    uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    uiState.studentSubmissionAttachments.removeAt(index)
                    uiState.copy(openProgressDialog = false)
                }

                is Resource.Error -> {
                    uiState.copy(
                        openProgressDialog = false,
                        userMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun submitAssignment() {
        if (courseId == null || courseWorkId == null || currentUser == null) {
            uiState = uiState.copy(userMessage = UiText.StringResource(Strings.error_unexpected))
            return
        }

        val userId = currentUser?.uid.orEmpty()
        val late = uiState.classwork.dueTime?.before(Date()) == true

        studentSubmission.value = studentSubmission.value.copy(
            courseId = courseId,
            courseWorkId = courseWorkId,
            id = userId,
            userId = userId,
            state = SubmissionState.TURNED_IN,
            late = late,
            alternateLink = "https://edumateapp.web.app/submissions?cid=$courseId&wid=$courseWorkId&id=$userId",
            courseWorkType = CourseWorkType.ASSIGNMENT,
            assignmentSubmission = AssignmentSubmission(
                attachments = uiState.studentSubmissionAttachments
            )
        )

        turnInStudentSubmissionUseCase(
            courseId,
            courseWorkId,
            userId,
            studentSubmission.value
        ).onEach { resource ->
            uiState = when (resource) {
                is Resource.Loading -> {
                    uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    fetchStudentSubmission()
                    uiState.copy(openProgressDialog = false)
                }

                is Resource.Error -> {
                    uiState.copy(
                        openProgressDialog = false,
                        userMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun unSubmitClasswork() {
        studentSubmission.value = studentSubmission.value.copy(
            state = SubmissionState.RECLAIMED_BY_STUDENT
        )

        turnInStudentSubmissionUseCase(
            studentSubmission.value.courseId,
            studentSubmission.value.courseWorkId,
            studentSubmission.value.id,
            studentSubmission.value
        ).onEach { resource ->
            uiState = when (resource) {
                is Resource.Loading -> {
                    uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    fetchStudentSubmission()
                    uiState.copy(openProgressDialog = false)
                }

                is Resource.Error -> {
                    uiState.copy(
                        openProgressDialog = false,
                        userMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}