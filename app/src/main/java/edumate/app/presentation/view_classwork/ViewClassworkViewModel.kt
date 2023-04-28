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
import edumate.app.core.utils.enumValueOf
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.domain.model.student_submission.AssignmentSubmission
import edumate.app.domain.model.student_submission.Attachment
import edumate.app.domain.model.student_submission.DriveFile
import edumate.app.domain.model.student_submission.MultipleChoiceSubmission
import edumate.app.domain.model.student_submission.ShortAnswerSubmission
import edumate.app.domain.model.student_submission.StudentSubmission
import edumate.app.domain.model.student_submission.SubmissionState
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.course_work.GetCourseWorkUseCase
import edumate.app.domain.usecase.storage.DeleteFileUseCase
import edumate.app.domain.usecase.storage.UploadFileUseCase
import edumate.app.domain.usecase.student_submission.GetStudentSubmissionUseCase
import edumate.app.domain.usecase.student_submission.ModifyAttachmentsStudentSubmission
import edumate.app.domain.usecase.student_submission.PatchStudentSubmission
import edumate.app.domain.usecase.student_submission.ReclaimStudentSubmission
import edumate.app.navigation.Routes
import edumate.app.presentation.class_details.UserType
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
    private val getStudentSubmissionUseCase: GetStudentSubmissionUseCase,
    private val patchStudentSubmission: PatchStudentSubmission,
    private val reclaimStudentSubmission: ReclaimStudentSubmission,
    private val modifyAttachmentsStudentSubmission: ModifyAttachmentsStudentSubmission
) : ViewModel() {

    var uiState by mutableStateOf(ViewClassworkUiState())
        private set

    private val courseId: String =
        checkNotNull(savedStateHandle[Routes.Args.VIEW_CLASSWORK_COURSE_ID])
    private val courseWorkId: String = checkNotNull(savedStateHandle[Routes.Args.VIEW_CLASSWORK_ID])
    private val workType: String = checkNotNull(savedStateHandle[Routes.Args.VIEW_CLASSWORK_TYPE])
    private val userType: String =
        checkNotNull(savedStateHandle[Routes.Args.VIEW_CLASSWORK_USER_TYPE])
    private var currentUser: FirebaseUser? = null
    private val studentSubmission = mutableStateOf(StudentSubmission())
    private var classworkType: CourseWorkType = CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED

    init {
        classworkType = workType.enumValueOf(CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED)!!
        val currentUserType: UserType = userType.enumValueOf(UserType.STUDENT)!!

        getCurrentUserUseCase().map { user ->
            currentUser = user
            if (user != null && currentUserType == UserType.STUDENT && (classworkType == CourseWorkType.ASSIGNMENT || classworkType == CourseWorkType.SHORT_ANSWER_QUESTION || classworkType == CourseWorkType.MULTIPLE_CHOICE_QUESTION)) {
                studentSubmission.value = studentSubmission.value.copy(
                    courseId = courseId,
                    courseWorkId = courseWorkId,
                    id = user.uid,
                    userId = user.uid,
                    state = SubmissionState.CREATED,
                    alternateLink = "${FirebaseConstants.Hosting.EDUMATEAPP}/submissions?cid=$courseId&cwid=$courseWorkId&id=${user.uid}",
                    courseWorkType = classworkType
                )

                fetchStudentSubmission()
            }
        }.launchIn(viewModelScope)

        fetchClasswork(false)
    }

    fun onEvent(event: ViewClassworkUiEvent) {
        when (event) {
            is ViewClassworkUiEvent.OnAppBarMenuExpandedChange -> {
                uiState = uiState.copy(appBarMenuExpanded = event.expanded)
            }

            is ViewClassworkUiEvent.OnFilePicked -> {
                uploadFile(event.uri, event.fileUtils)
            }

            is ViewClassworkUiEvent.OnOpenRemoveAttachmentDialog -> {
                uiState = uiState.copy(openRemoveAttachmentDialog = event.index)
            }

            is ViewClassworkUiEvent.OnOpenTurnInDialog -> {
                uiState = uiState.copy(openTurnInDialog = event.open)
            }

            is ViewClassworkUiEvent.OnOpenUnSubmitDialog -> {
                uiState = uiState.copy(openUnSubmitDialog = event.open)
            }

            is ViewClassworkUiEvent.OnOpenYourWorkBottomSheet -> {
                uiState = uiState.copy(openYourWorkBottomSheet = event.open)
            }

            is ViewClassworkUiEvent.OnRemoveAttachment -> {
                deleteFile(event.index)
            }

            ViewClassworkUiEvent.OnRefresh -> {
                fetchStudentSubmission()
                fetchClasswork(true)
            }

            ViewClassworkUiEvent.TurnIn -> {
                submitStudentSubmission()
            }

            ViewClassworkUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }

            ViewClassworkUiEvent.UnSubmit -> {
                unSubmitStudentSubmission()
            }
        }
    }

    private fun fetchClasswork(refreshing: Boolean) {
        getCourseWorkUseCase(courseId, courseWorkId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = if (refreshing) {
                        uiState.copy(refreshing = true)
                    } else {
                        uiState.copy(dataState = DataState.LOADING)
                    }
                }

                is Resource.Success -> {
                    val classwork = resource.data
                    uiState = if (classwork != null) {
                        uiState.copy(
                            classwork = classwork,
                            dataState = DataState.SUCCESS,
                            refreshing = false
                        )
                    } else {
                        if (refreshing) {
                            uiState.copy(
                                refreshing = false,
                                userMessage = UiText.StringResource(Strings.error_unexpected)
                            )
                        } else {
                            uiState.copy(
                                dataState = DataState.EMPTY(
                                    UiText.StringResource(Strings.error_unexpected)
                                )
                            )
                        }
                    }
                }

                is Resource.Error -> {
                    uiState = if (refreshing) {
                        uiState.copy(
                            refreshing = false,
                            userMessage = resource.message
                        )
                    } else {
                        uiState.copy(dataState = DataState.ERROR(message = resource.message!!))
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun createStudentSubmission() {
        patchStudentSubmission(
            courseId,
            courseWorkId,
            studentSubmission.value.id,
            studentSubmission.value
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    val submission = resource.data
                    if (submission != null) {
                        studentSubmission.value = submission
                        uiState.studentSubmissionAttachments.clear()
                        uiState.studentSubmissionAttachments.addAll(
                            submission.assignmentSubmission?.attachments.orEmpty()
                        )
                        uiState = uiState.copy(
                            studentSubmission = submission,
                            yourWorkDataState = DataState.SUCCESS
                        )
                    } else {
                        uiState = uiState.copy(
                            yourWorkDataState = DataState.EMPTY(
                                UiText.StringResource(Strings.error_unexpected)
                            )
                        )
                    }
                }

                is Resource.Error -> {
                    uiState =
                        uiState.copy(yourWorkDataState = DataState.ERROR(resource.message!!))
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchStudentSubmission() {
        getStudentSubmissionUseCase(
            courseId,
            courseWorkId,
            currentUser!!.uid
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(yourWorkDataState = DataState.LOADING)
                }

                is Resource.Success -> {
                    val submission = resource.data
                    if (submission != null) {
                        studentSubmission.value = submission
                        uiState.studentSubmissionAttachments.clear()
                        uiState.studentSubmissionAttachments.addAll(
                            submission.assignmentSubmission?.attachments.orEmpty()
                        )
                        uiState = uiState.copy(
                            studentSubmission = submission,
                            yourWorkDataState = DataState.SUCCESS
                        )
                    } else {
                        createStudentSubmission()
                    }
                }

                is Resource.Error -> {
                    uiState =
                        uiState.copy(yourWorkDataState = DataState.ERROR(resource.message!!))
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun submitStudentSubmission() {
        val late = uiState.classwork.dueTime?.before(Date()) == true
        var assignmentSubmission: AssignmentSubmission? = null
        var shortAnswerSubmission: ShortAnswerSubmission? = null
        var multipleChoiceSubmission: MultipleChoiceSubmission? = null

        when (classworkType) {
            CourseWorkType.MATERIAL -> {}
            CourseWorkType.ASSIGNMENT -> {
                assignmentSubmission =
                    AssignmentSubmission(attachments = uiState.studentSubmissionAttachments)
            }

            CourseWorkType.SHORT_ANSWER_QUESTION -> {
                shortAnswerSubmission = ShortAnswerSubmission(answer = "")
            }

            CourseWorkType.MULTIPLE_CHOICE_QUESTION -> {
                multipleChoiceSubmission = MultipleChoiceSubmission(answer = "")
            }

            else -> {
                uiState =
                    uiState.copy(userMessage = UiText.StringResource(Strings.error_unexpected))
                return
            }
        }

        studentSubmission.value = studentSubmission.value.copy(
            state = SubmissionState.TURNED_IN,
            late = late,
            assignmentSubmission = assignmentSubmission,
            shortAnswerSubmission = shortAnswerSubmission,
            multipleChoiceSubmission = multipleChoiceSubmission
        )

        patchStudentSubmission(
            courseId,
            courseWorkId,
            studentSubmission.value.id,
            studentSubmission.value
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    val submission = resource.data
                    if (submission != null) {
                        studentSubmission.value = submission
                        uiState.studentSubmissionAttachments.clear()
                        uiState.studentSubmissionAttachments.addAll(
                            submission.assignmentSubmission?.attachments.orEmpty()
                        )
                        uiState = uiState.copy(
                            openProgressDialog = false,
                            studentSubmission = submission
                        )
                    } else {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                yourWorkDataState = DataState.EMPTY(
                                    UiText.StringResource(Strings.error_unexpected)
                                )
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

    private fun unSubmitStudentSubmission() {
        reclaimStudentSubmission(
            courseId,
            courseWorkId,
            studentSubmission.value.id
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    uiState = uiState.copy(openProgressDialog = false)
                    fetchStudentSubmission()
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
                    if (fileUrl != null) {
                        val driveFile = DriveFile(
                            url = fileUrl.toString(),
                            title = fileName,
                            type = mimeType
                        )
                        uiState.studentSubmissionAttachments.add(Attachment(driveFile))
                        modifyAttachments()
                    } else {
                        uiState = uiState.copy(
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
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    uiState.studentSubmissionAttachments.removeAt(index)
                    modifyAttachments()
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

    private fun modifyAttachments() {
        modifyAttachmentsStudentSubmission(
            courseId,
            courseWorkId,
            studentSubmission.value.id,
            uiState.studentSubmissionAttachments
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    val submission = resource.data
                    if (submission != null) {
                        studentSubmission.value = submission
                        uiState = uiState.copy(
                            openProgressDialog = false,
                            studentSubmission = submission
                        )
                    } else {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                yourWorkDataState = DataState.EMPTY(
                                    UiText.StringResource(Strings.error_unexpected)
                                )
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
}