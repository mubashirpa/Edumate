package edumate.app.presentation.create_classwork

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.FirebaseConstants
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.core.utils.FileUtils
import edumate.app.core.utils.enumValueOf
import edumate.app.domain.model.AssigneeMode
import edumate.app.domain.model.DriveFile
import edumate.app.domain.model.Link
import edumate.app.domain.model.Material
import edumate.app.domain.model.course_work.Assignment
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.course_work.CourseWorkState
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.domain.model.course_work.MultipleChoiceQuestion
import edumate.app.domain.model.course_work.SubmissionModificationMode
import edumate.app.domain.usecase.GetUrlMetadataUseCase
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.course_work.CreateCourseWork
import edumate.app.domain.usecase.course_work.GetCourseWork
import edumate.app.domain.usecase.course_work.PatchCourseWork
import edumate.app.domain.usecase.notification.SendNotificationUseCase
import edumate.app.domain.usecase.storage.DeleteFileUseCase
import edumate.app.domain.usecase.storage.UploadFileUseCase
import edumate.app.domain.usecase.students.ListStudents
import edumate.app.domain.usecase.validation.ValidateTextField
import edumate.app.navigation.Routes
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import edumate.app.R.string as Strings

@HiltViewModel
class CreateClassworkViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        getCurrentUserUseCase: GetCurrentUserUseCase,
        private val getUrlMetadataUseCase: GetUrlMetadataUseCase,
        private val createCourseWorkUseCase: CreateCourseWork,
        private val getCourseWorkUseCase: GetCourseWork,
        private val patchCourseWorkUseCase: PatchCourseWork,
        private val uploadFileUseCase: UploadFileUseCase,
        private val deleteFileUseCase: DeleteFileUseCase,
        private val validateTextField: ValidateTextField,
        private val sendNotificationUseCase: SendNotificationUseCase,
        private val listStudentsUseCase: ListStudents,
    ) : ViewModel() {
        var uiState by mutableStateOf(CreateClassworkUiState())
            private set

        private val resultChannel = Channel<String>()
        val createClassworkResults = resultChannel.receiveAsFlow()

        private val courseId: String =
            checkNotNull(savedStateHandle[Routes.Args.CREATE_CLASSWORK_COURSE_ID])
        private val classworkId: String =
            checkNotNull(savedStateHandle[Routes.Args.CREATE_CLASSWORK_ID])
        private val type: String = checkNotNull(savedStateHandle[Routes.Args.CREATE_CLASSWORK_TYPE])
        private val courseWork = mutableStateOf(CourseWork())
        private var getUrlMetadataJob: Job? = null
        private var currentUser: FirebaseUser? = null

        init {
            val workType: CourseWorkType =
                type.enumValueOf(CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED)!!
            uiState = uiState.copy(workType = workType)
            val id = generateClassworkId()
            val urlWorkType =
                when (workType) {
                    CourseWorkType.ASSIGNMENT -> "a"
                    CourseWorkType.SHORT_ANSWER_QUESTION -> "sa"
                    CourseWorkType.MULTIPLE_CHOICE_QUESTION -> "mc"
                    else -> "m"
                }

            courseWork.value =
                courseWork.value.copy(
                    courseId = courseId,
                    id = id,
                    state = CourseWorkState.PUBLISHED,
                    alternateLink = "${FirebaseConstants.Hosting.EDUMATEAPP}/c/$urlWorkType/details?cid=$courseId&cwid=$id",
                    assigneeMode = AssigneeMode.ALL_STUDENTS,
                    submissionModificationMode = SubmissionModificationMode.MODIFIABLE,
                )

            getCurrentUserUseCase().map { user ->
                currentUser = user
                if (user != null) {
                    courseWork.value = courseWork.value.copy(creatorUserId = user.uid)
                }
            }.launchIn(viewModelScope)

            if (classworkId != "null") {
                fetchClasswork()
            }
        }

        fun onEvent(event: CreateClassworkUiEvent) {
            when (event) {
                is CreateClassworkUiEvent.OnAddLinkAttachment -> {
                    val link =
                        Link(
                            url = event.link,
                            title = event.link,
                        )
                    uiState.attachments.add(Material(link = link))
                    fetchUrlMetadata(event.link)
                }

                is CreateClassworkUiEvent.OnDescriptionChange -> {
                    uiState = uiState.copy(description = event.description)
                }

                is CreateClassworkUiEvent.OnDueDateChange -> {
                    uiState = uiState.copy(dueDate = event.dueDate)
                }

                is CreateClassworkUiEvent.OnFilePicked -> {
                    uploadFile(event.uri, event.fileUtils)
                }

                is CreateClassworkUiEvent.OnOpenAddLinkDialogChange -> {
                    uiState = uiState.copy(openAddLinkDialog = event.open)
                }

                is CreateClassworkUiEvent.OnOpenAttachmentMenuChange -> {
                    uiState = uiState.copy(openAttachmentMenu = event.open)
                }

                is CreateClassworkUiEvent.OnOpenDatePickerDialogChange -> {
                    uiState = uiState.copy(openDatePickerDialog = event.open)
                }

                is CreateClassworkUiEvent.OnOpenPointsDialogChange -> {
                    uiState = uiState.copy(openPointsDialog = event.open)
                }

                is CreateClassworkUiEvent.OnOpenTimePickerDialogChange -> {
                    uiState = uiState.copy(openTimePickerDialog = event.open)
                }

                is CreateClassworkUiEvent.OnPointsChange -> {
                    uiState = uiState.copy(points = event.points)
                }

                is CreateClassworkUiEvent.OnTitleChange -> {
                    uiState =
                        uiState.copy(
                            title = event.title,
                            titleError = null,
                        )
                }

                is CreateClassworkUiEvent.OnRemoveAttachment -> {
                    val attachment = uiState.attachments[event.position]
                    when {
                        attachment.driveFile != null -> {
                            deleteFile(event.position)
                        }

                        attachment.link != null -> {
                            // Stop urlUseCaseJob to avoid conflict
                            getUrlMetadataJob?.cancel()
                            uiState.attachments.removeAt(event.position)
                        }
                    }
                }

                is CreateClassworkUiEvent.OnWorkTypeChange -> {
                    // Empty choices when change workType
                    uiState =
                        uiState.copy(
                            workType = event.workType,
                            choices = mutableStateListOf("Option 1"),
                        )
                }

                CreateClassworkUiEvent.CreateClasswork -> {
                    if (classworkId != "null") {
                        updateClasswork()
                    } else {
                        createClasswork()
                    }
                }

                CreateClassworkUiEvent.UserMessageShown -> {
                    uiState = uiState.copy(userMessage = null)
                }
            }
        }

        private fun createClasswork() {
            val title = uiState.title
            val titleResult = validateTextField.execute(title)

            if (!titleResult.successful) {
                uiState = uiState.copy(titleError = UiText.StringResource(Strings.missing_title))
                return
            }

            val maxPoints =
                try {
                    uiState.points?.toInt()
                } catch (e: NumberFormatException) {
                    null
                }
            val multipleChoiceQuestion =
                if (uiState.workType == CourseWorkType.MULTIPLE_CHOICE_QUESTION) {
                    MultipleChoiceQuestion(choices = uiState.choices)
                } else {
                    null
                }
            val assignment =
                if (uiState.workType == CourseWorkType.ASSIGNMENT) {
                    Assignment(
                        studentWorkFolder = "${FirebaseConstants.Storage.COURSE_STORAGE_PATH}/$courseId/course_work/${courseWork.value.id}",
                    )
                } else {
                    null
                }

            courseWork.value =
                courseWork.value.copy(
                    title = title,
                    description = uiState.description.ifEmpty { null },
                    materials = uiState.attachments,
                    dueTime = uiState.dueDate,
                    maxPoints = maxPoints,
                    workType = uiState.workType,
                    assignment = assignment,
                    multipleChoiceQuestion = multipleChoiceQuestion,
                )

            createCourseWorkUseCase(courseId, courseWork.value).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }

                    is Resource.Success -> {
                        val classwork = resource.data
                        if (classwork != null) {
                            fetchStudents(classwork)
                        } else {
                            uiState =
                                uiState.copy(
                                    openProgressDialog = false,
                                    userMessage = UiText.StringResource(Strings.error_unexpected),
                                )
                        }
                    }

                    is Resource.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = resource.message,
                            )
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun fetchClasswork() {
            getCourseWorkUseCase(courseId, classworkId).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        uiState = uiState.copy(loading = true)
                    }

                    is Resource.Success -> {
                        val classwork = resource.data
                        if (classwork != null) {
                            courseWork.value = classwork
                            val choices = classwork.multipleChoiceQuestion?.choices
                            if (choices != null) {
                                uiState.choices.clear()
                                uiState.choices.addAll(choices)
                            }
                            uiState.attachments.addAll(classwork.materials)

                            uiState =
                                uiState.copy(
                                    description = classwork.description.orEmpty(),
                                    dueDate = classwork.dueTime,
                                    loading = false,
                                    points = classwork.maxPoints?.toString(),
                                    title = classwork.title,
                                    workType = classwork.workType,
                                )
                        } else {
                            uiState =
                                uiState.copy(
                                    loading = false,
                                    userMessage = UiText.StringResource(Strings.error_unexpected),
                                )
                        }
                    }

                    is Resource.Error -> {
                        uiState =
                            uiState.copy(
                                loading = false,
                                userMessage = resource.message,
                            )
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun updateClasswork() {
            val title = uiState.title
            val titleResult = validateTextField.execute(title)

            if (!titleResult.successful) {
                uiState = uiState.copy(titleError = UiText.StringResource(Strings.missing_title))
                return
            }

            val maxPoints =
                try {
                    uiState.points?.toInt()
                } catch (e: NumberFormatException) {
                    null
                }
            val multipleChoiceQuestion =
                if (uiState.workType == CourseWorkType.MULTIPLE_CHOICE_QUESTION) {
                    MultipleChoiceQuestion(choices = uiState.choices)
                } else {
                    null
                }
            val assignment =
                if (uiState.workType == CourseWorkType.ASSIGNMENT) {
                    Assignment(
                        studentWorkFolder = "${FirebaseConstants.Storage.COURSE_STORAGE_PATH}/$courseId/course_work/${courseWork.value.id}",
                    )
                } else {
                    null
                }

            courseWork.value =
                courseWork.value.copy(
                    title = title,
                    description = uiState.description.ifEmpty { null },
                    materials = uiState.attachments,
                    dueTime = uiState.dueDate,
                    maxPoints = maxPoints,
                    workType = uiState.workType,
                    assignment = assignment,
                    multipleChoiceQuestion = multipleChoiceQuestion,
                )

            patchCourseWorkUseCase(courseId, courseWork.value.id, courseWork.value).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }

                    is Resource.Success -> {
                        val classwork = resource.data
                        if (classwork != null) {
                            uiState = uiState.copy(openProgressDialog = false)
                            resultChannel.send(classwork.id)
                        } else {
                            uiState =
                                uiState.copy(
                                    openProgressDialog = false,
                                    userMessage = UiText.StringResource(Strings.error_unexpected),
                                )
                        }
                    }

                    is Resource.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = resource.message,
                            )
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun uploadFile(
            uri: Uri,
            fileUtils: FileUtils,
        ) {
            val fileExtension = fileUtils.getFileExtension(uri)
            val fileName = fileUtils.getFileName(uri) ?: "${uri.lastPathSegment}.$fileExtension"
            val mimeType = fileUtils.getMimeType(uri)
            val filePath =
                "${FirebaseConstants.Storage.COURSE_STORAGE_PATH}/$courseId/courseWork/${courseWork.value.id}/$fileName"

            uploadFileUseCase(uri, filePath).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }

                    is Resource.Success -> {
                        val fileUrl = resource.data
                        uiState =
                            if (fileUrl != null) {
                                val driveFile =
                                    DriveFile(
                                        url = fileUrl.toString(),
                                        title = fileName,
                                        type = mimeType,
                                    )
                                uiState.attachments.add(Material(driveFile = driveFile))
                                uiState.copy(openProgressDialog = false)
                            } else {
                                uiState.copy(
                                    openProgressDialog = false,
                                    userMessage = UiText.StringResource(Strings.error_unexpected),
                                )
                            }
                    }

                    is Resource.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = resource.message,
                            )
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun deleteFile(position: Int) {
            val fileName = uiState.attachments[position].driveFile?.title
            val filePath =
                "${FirebaseConstants.Storage.COURSE_STORAGE_PATH}/$courseId/courseWork/${courseWork.value.id}/$fileName"

            deleteFileUseCase(filePath).onEach { resource ->
                uiState =
                    when (resource) {
                        is Resource.Loading -> {
                            uiState.copy(openProgressDialog = true)
                        }

                        is Resource.Success -> {
                            // Stop urlUseCaseJob to avoid problems
                            getUrlMetadataJob?.cancel()
                            uiState.attachments.removeAt(position)
                            uiState.copy(openProgressDialog = false)
                        }

                        is Resource.Error -> {
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = resource.message,
                            )
                        }
                    }
            }.launchIn(viewModelScope)
        }

        private fun fetchStudents(classwork: CourseWork) {
            listStudentsUseCase(courseId).onEach { resource ->
                if (resource is Resource.Success) {
                    val students = resource.data
                    if (!students.isNullOrEmpty()) {
                        val studentIds: List<String> = students.map { it.id }
                        sendNotification(
                            currentUser?.displayName.orEmpty(),
                            classwork.workType,
                            classwork.title,
                            studentIds,
                        )
                    }
                    uiState = uiState.copy(openProgressDialog = false)
                    resultChannel.send(classwork.id)
                } else if (resource is Resource.Error) {
                    uiState = uiState.copy(openProgressDialog = false)
                    resultChannel.send(classwork.id)
                }
            }.launchIn(viewModelScope)
        }

        private fun fetchUrlMetadata(url: String) {
            val position = uiState.attachments.lastIndex
            getUrlMetadataJob =
                getUrlMetadataUseCase(url).onEach { resource ->
                    if (resource is Resource.Success) {
                        uiState.attachments[position] = Material(link = resource.data)
                    }
                }.launchIn(viewModelScope)
        }

        private fun generateClassworkId(): String {
            return FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.Firestore.COURSES_COLLECTION).document(courseId)
                .collection(FirebaseConstants.Firestore.COURSE_WORK_COLLECTION).document().id
        }

        private suspend fun sendNotification(
            userName: String,
            courseWorkType: CourseWorkType,
            courseWorkTitle: String,
            studentIds: List<String>,
        ) {
            val title =
                when (courseWorkType) {
                    CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED -> {
                        ""
                    }

                    CourseWorkType.MATERIAL -> {
                        "New material from $userName"
                    }

                    CourseWorkType.ASSIGNMENT -> {
                        "New assignment from $userName"
                    }

                    else -> {
                        "New question from $userName"
                    }
                }
            val description =
                when (courseWorkType) {
                    CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED -> {
                        ""
                    }

                    CourseWorkType.MATERIAL -> {
                        "$courseWorkTitle • See details"
                    }

                    CourseWorkType.ASSIGNMENT -> {
                        "$courseWorkTitle • See details"
                    }

                    else -> {
                        "$courseWorkTitle • Answer question"
                    }
                }
            sendNotificationUseCase(title, description, studentIds)
        }
    }
