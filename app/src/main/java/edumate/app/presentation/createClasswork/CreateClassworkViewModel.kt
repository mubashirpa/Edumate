package edumate.app.presentation.createClasswork

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Firebase
import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.core.utils.DatabaseUtils
import edumate.app.core.utils.enumValueOf
import edumate.app.domain.model.classroom.DriveFile
import edumate.app.domain.model.classroom.Link
import edumate.app.domain.model.classroom.Material
import edumate.app.domain.model.classroom.courseWork.CourseWork
import edumate.app.domain.model.classroom.courseWork.CourseWorkState
import edumate.app.domain.model.classroom.courseWork.CourseWorkType
import edumate.app.domain.model.classroom.courseWork.DueDate
import edumate.app.domain.model.classroom.courseWork.DueTime
import edumate.app.domain.model.classroom.courseWork.MultipleChoiceQuestion
import edumate.app.domain.usecase.GetUrlMetadataUseCase
import edumate.app.domain.usecase.classroom.courseWork.CreateCourseWorkUseCase
import edumate.app.domain.usecase.classroom.courseWork.GetCourseWorkUseCase
import edumate.app.domain.usecase.classroom.courseWork.PatchCourseWorkUseCase
import edumate.app.domain.usecase.storage.DeleteFileUseCase
import edumate.app.domain.usecase.storage.UploadFileUseCase
import edumate.app.domain.usecase.validation.ValidateTextField
import edumate.app.navigation.Routes
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import edumate.app.R.string as Strings

@HiltViewModel
class CreateClassworkViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val createCourseWorkUseCase: CreateCourseWorkUseCase,
        private val deleteFileUseCase: DeleteFileUseCase,
        private val getCourseWorkUseCase: GetCourseWorkUseCase,
        private val getUrlMetadataUseCase: GetUrlMetadataUseCase,
        private val patchCourseWorkUseCaseUseCase: PatchCourseWorkUseCase,
        private val uploadFileUseCase: UploadFileUseCase,
        private val validateTextField: ValidateTextField,
    ) : ViewModel() {
        var uiState by mutableStateOf(CreateClassworkUiState())
            private set

        private val courseId: String =
            checkNotNull(savedStateHandle[Routes.Args.CREATE_CLASSWORK_COURSE_ID])
        private val workType: String = checkNotNull(savedStateHandle[Routes.Args.CREATE_CLASSWORK_TYPE])
        private val courseWorkId: String? = savedStateHandle[Routes.Args.CREATE_CLASSWORK_ID]

        private val courseWork =
            mutableStateOf(CourseWork(id = courseWorkId ?: DatabaseUtils.generateId(12)))
        private var getUrlMetadataJob: Job? = null

        init {
            uiState = uiState.copy(workType = enumValueOf(workType))
            if (courseWorkId != null) {
                getCourseWork(courseWorkId)
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

                is CreateClassworkUiEvent.OnDescriptionValueChange -> {
                    uiState = uiState.copy(description = event.description)
                }

                is CreateClassworkUiEvent.OnDueDateTimeValueChange -> {
                    uiState = uiState.copy(dueDateTime = event.dateTime)
                }

                is CreateClassworkUiEvent.OnFilePicked -> {
                    uploadFile(event.uri, event.title, courseWork.value.id!!)
                }

                is CreateClassworkUiEvent.OnOpenAddLinkDialogChange -> {
                    uiState = uiState.copy(openAddLinkDialog = event.open)
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

                is CreateClassworkUiEvent.OnPointsValueChange -> {
                    uiState = uiState.copy(points = event.points)
                }

                is CreateClassworkUiEvent.OnQuestionTypeDropdownExpandedChange -> {
                    uiState = uiState.copy(questionTypeDropdownExpanded = event.expanded)
                }

                is CreateClassworkUiEvent.OnQuestionTypeValueChange -> {
                    val questionType =
                        when (event.selectionOptionIndex) {
                            0 -> CourseWorkType.SHORT_ANSWER_QUESTION
                            else -> CourseWorkType.MULTIPLE_CHOICE_QUESTION
                        }

                    // Empty choices when change workType
                    uiState =
                        uiState.copy(
                            choices = mutableStateListOf("Option 1"),
                            questionTypeDropdownExpanded = false,
                            questionTypeSelectionOptionIndex = event.selectionOptionIndex,
                            workType = questionType,
                        )
                }

                is CreateClassworkUiEvent.OnRemoveAttachment -> {
                    val attachment = uiState.attachments[event.position]
                    when {
                        attachment.driveFile != null -> {
                            deleteFile(event.position, courseWork.value.id!!)
                        }

                        attachment.link != null -> {
                            // Cancel getUrlMetadataJob to prevent potential conflicts.
                            // If getUrlMetadataJob is running in the background and succeeds,
                            // it may re-add the deleted data.
                            getUrlMetadataJob?.cancel()
                            uiState.attachments.removeAt(event.position)
                        }
                    }
                }

                is CreateClassworkUiEvent.OnShowAddAttachmentBottomSheetChange -> {
                    uiState = uiState.copy(showAddAttachmentBottomSheet = event.show)
                }

                is CreateClassworkUiEvent.OnTitleValueChange -> {
                    uiState =
                        uiState.copy(
                            title = event.title,
                            titleError = null,
                        )
                }

                CreateClassworkUiEvent.CreateCourseWork -> {
                    if (courseWorkId != null) {
                        patchCourseWork(courseWorkId)
                    } else {
                        createCourseWork()
                    }
                }

                CreateClassworkUiEvent.UserMessageShown -> {
                    uiState = uiState.copy(userMessage = null)
                }
            }
        }

        private fun createCourseWork() {
            val title = uiState.title.text.trim()
            val workType = uiState.workType
            val description = uiState.description.trim().ifEmpty { null }
            val dueDateTime = uiState.dueDateTime
            var dueDate: DueDate? = null
            var dueTime: DueTime? = null
            if (dueDateTime != null) {
                dueDate =
                    DueDate(
                        day = dueDateTime.dayOfMonth,
                        month = dueDateTime.monthNumber,
                        year = dueDateTime.year,
                    )
                dueTime =
                    DueTime(
                        hours = dueDateTime.hour,
                        minutes = dueDateTime.minute,
                        nanos = dueDateTime.nanosecond,
                        seconds = dueDateTime.second,
                    )
            }
            val materials = uiState.attachments
            val maxPoints =
                try {
                    uiState.points?.toInt()
                } catch (e: NumberFormatException) {
                    null
                }
            val multipleChoiceQuestion =
                if (workType == CourseWorkType.MULTIPLE_CHOICE_QUESTION) {
                    MultipleChoiceQuestion(choices = uiState.choices)
                } else {
                    null
                }
            val state = CourseWorkState.PUBLISHED

            val titleResult = validateTextField.execute(title)
            if (!titleResult.successful) {
                uiState = uiState.copy(titleError = UiText.StringResource(Strings.missing_title))
                return
            }
            val isQuestion =
                workType == CourseWorkType.SHORT_ANSWER_QUESTION || workType == CourseWorkType.MULTIPLE_CHOICE_QUESTION
            if (isQuestion && uiState.questionTypeSelectionOptionIndex == null) {
                uiState =
                    uiState.copy(userMessage = UiText.StringResource(Strings.missing_question_type))
                return
            }

            courseWork.value =
                courseWork.value.copy(
                    description = description,
                    dueTime = dueTime,
                    dueDate = dueDate,
                    materials = materials,
                    maxPoints = maxPoints,
                    multipleChoiceQuestion = multipleChoiceQuestion,
                    state = state,
                    title = title,
                    workType = workType,
                )

            createCourseWorkUseCase(courseId, courseWork.value).onEach { result ->
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
                        val courseWorkResponse = result.data
                        uiState =
                            if (courseWorkResponse != null) {
                                uiState.copy(
                                    isCreateClassworkSuccess = true,
                                    openProgressDialog = false,
                                )
                            } else {
                                uiState.copy(
                                    openProgressDialog = false,
                                    userMessage = UiText.StringResource(Strings.error_unexpected),
                                )
                            }
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun getCourseWork(id: String) {
            getCourseWorkUseCase(courseId, id).onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                isLoading = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        uiState = uiState.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        val courseWorkResponse = result.data
                        if (courseWorkResponse != null) {
                            courseWork.value = courseWorkResponse
                            val choices = courseWorkResponse.multipleChoiceQuestion?.choices.orEmpty()
                            if (choices.isNotEmpty()) {
                                uiState.choices.clear()
                                uiState.choices.addAll(choices)
                            }
                            uiState.attachments.addAll(courseWorkResponse.materials.orEmpty())
                            val dueDate = courseWorkResponse.dueDate
                            val dueTime = courseWorkResponse.dueTime
                            val dueDateTime =
                                if (dueDate != null) {
                                    val currentDateTime =
                                        Clock.System.now()
                                            .toLocalDateTime(TimeZone.currentSystemDefault())
                                    LocalDateTime(
                                        dueDate.year ?: currentDateTime.year,
                                        dueDate.month ?: currentDateTime.monthNumber,
                                        dueDate.day ?: currentDateTime.dayOfMonth,
                                        dueTime!!.hours ?: currentDateTime.hour,
                                        dueTime.minutes ?: currentDateTime.minute,
                                        dueTime.seconds ?: currentDateTime.second,
                                        dueTime.nanos ?: currentDateTime.nanosecond,
                                    )
                                } else {
                                    null
                                }
                            val title = courseWorkResponse.title.orEmpty()
                            val questionTypeSelectionOptionIndex =
                                when (courseWorkResponse.workType) {
                                    CourseWorkType.SHORT_ANSWER_QUESTION -> 0
                                    else -> 1
                                }

                            uiState =
                                uiState.copy(
                                    description = courseWorkResponse.description.orEmpty(),
                                    dueDateTime = dueDateTime,
                                    isLoading = false,
                                    points = courseWorkResponse.maxPoints?.toString(),
                                    questionTypeSelectionOptionIndex = questionTypeSelectionOptionIndex,
                                    title =
                                        TextFieldValue(
                                            text = title,
                                            selection = TextRange(title.length),
                                        ),
                                    workType = courseWorkResponse.workType,
                                )
                        } else {
                            uiState =
                                uiState.copy(
                                    isLoading = false,
                                    userMessage = UiText.StringResource(Strings.error_unexpected),
                                )
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun patchCourseWork(id: String) {
            val title = uiState.title.text.trim()
            val description = uiState.description.trim().ifEmpty { null }
            val dueDateTime = uiState.dueDateTime
            var dueDate: DueDate? = null
            var dueTime: DueTime? = null
            if (dueDateTime != null) {
                dueTime =
                    DueTime(
                        hours = dueDateTime.hour,
                        minutes = dueDateTime.minute,
                        nanos = dueDateTime.nanosecond,
                        seconds = dueDateTime.second,
                    )
                dueDate =
                    DueDate(
                        day = dueDateTime.dayOfMonth,
                        month = dueDateTime.monthNumber,
                        year = dueDateTime.year,
                    )
            }
            val maxPoints =
                try {
                    uiState.points?.toInt()
                } catch (e: NumberFormatException) {
                    null
                }
            val scheduledTime = null // TODO("Not yet implemented")
            val topicId = null // TODO("Not yet implemented")

            val materials = uiState.attachments
            val multipleChoiceQuestion =
                if (uiState.workType == CourseWorkType.MULTIPLE_CHOICE_QUESTION) {
                    MultipleChoiceQuestion(choices = uiState.choices)
                } else {
                    null
                }

            val titleResult = validateTextField.execute(title)
            if (!titleResult.successful) {
                uiState = uiState.copy(titleError = UiText.StringResource(Strings.missing_title))
                return
            }

            val updateMask =
                StringBuilder("").apply {
                    if (title != courseWork.value.title) {
                        append("title,")
                    }
                    if (description != courseWork.value.description) {
                        append("description,")
                    }
                    if (dueDate?.equals(courseWork.value.dueDate) == false) {
                        append("dueDate,")
                    }
                    if (dueTime?.equals(courseWork.value.dueTime) == false) {
                        append("dueTime,")
                    }
                    if (maxPoints != courseWork.value.maxPoints) {
                        append("maxPoints,")
                    }
                    if (scheduledTime != courseWork.value.scheduledTime) {
                        append("scheduledTime,")
                    }
                    if (topicId != courseWork.value.topicId) {
                        append("topicId,")
                    }
                }

            if (updateMask.isEmpty()) {
                uiState = uiState.copy(isCreateClassworkSuccess = true)
                return
            }

            courseWork.value =
                courseWork.value.copy(
                    description = description,
                    dueTime = dueTime,
                    dueDate = dueDate,
                    materials = materials,
                    maxPoints = maxPoints,
                    multipleChoiceQuestion = multipleChoiceQuestion,
                    scheduledTime = scheduledTime,
                    title = title,
                    topicId = topicId,
                )

            patchCourseWorkUseCaseUseCase(
                courseId,
                id,
                updateMask.toString(),
                courseWork.value,
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
                        val courseWorkResponse = result.data
                        uiState =
                            if (courseWorkResponse != null) {
                                uiState.copy(
                                    isCreateClassworkSuccess = true,
                                    openProgressDialog = false,
                                )
                            } else {
                                uiState.copy(
                                    openProgressDialog = false,
                                    userMessage = UiText.StringResource(Strings.error_unexpected),
                                )
                            }
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun uploadFile(
            uri: Uri,
            title: String,
            courseWorkId: String,
        ) {
            val filePath =
                Firebase.Storage.COURSE_WORK_MATERIALS_PATH
                    .replace("{courseId}", courseId)
                    .replace("{id}", courseWorkId)
                    .plus("/$title")

            uploadFileUseCase(uri, filePath).onEach { result ->
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
                        val fileUrl = result.data
                        uiState =
                            if (fileUrl != null) {
                                val driveFile =
                                    DriveFile(
                                        alternateLink = fileUrl.toString(),
                                        title = title,
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
                }
            }.launchIn(viewModelScope)
        }

        private fun deleteFile(
            position: Int,
            courseWorkId: String,
        ) {
            val fileName = uiState.attachments[position].driveFile?.title
            val filePath =
                Firebase.Storage.COURSE_WORK_MATERIALS_PATH
                    .replace("{courseId}", courseId)
                    .replace("{id}", courseWorkId)
                    .plus("/$fileName")

            deleteFileUseCase(filePath).onEach { result ->
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
                        uiState.attachments.removeAt(position)
                        uiState = uiState.copy(openProgressDialog = false)
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun fetchUrlMetadata(url: String) {
            val position = uiState.attachments.lastIndex
            getUrlMetadataJob =
                getUrlMetadataUseCase(url).onEach { result ->
                    if (result is Result.Success) {
                        uiState.attachments[position] = Material(link = result.data)
                    }
                }.launchIn(viewModelScope)
        }
    }
