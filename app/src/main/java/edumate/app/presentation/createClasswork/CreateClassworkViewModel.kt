package edumate.app.presentation.createClasswork

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.FirebaseConstants
import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.core.utils.FileUtils
import edumate.app.core.utils.enumValueOf
import edumate.app.domain.model.classroom.DriveFile
import edumate.app.domain.model.classroom.Link
import edumate.app.domain.model.classroom.Material
import edumate.app.domain.model.classroom.courseWork.CourseWork
import edumate.app.domain.model.classroom.courseWork.CourseWorkType
import edumate.app.domain.model.classroom.courseWork.DueDate
import edumate.app.domain.model.classroom.courseWork.DueTime
import edumate.app.domain.model.classroom.courseWork.MultipleChoiceQuestion
import edumate.app.domain.usecase.GetUrlMetadataUseCase
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.classroom.courseWork.CreateCourseWorkUseCase
import edumate.app.domain.usecase.classroom.courseWork.GetCourseWorkUseCase
import edumate.app.domain.usecase.classroom.courseWork.UpdateCourseWorkUseCase
import edumate.app.domain.usecase.storage.DeleteFileUseCase
import edumate.app.domain.usecase.storage.UploadFileUseCase
import edumate.app.domain.usecase.validation.ValidateTextField
import edumate.app.navigation.Routes
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.Calendar
import javax.inject.Inject
import edumate.app.R.string as Strings

@HiltViewModel
class CreateClassworkViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        getCurrentUserUseCase: GetCurrentUserUseCase,
        private val createCourseWorkUseCase: CreateCourseWorkUseCase,
        private val deleteFileUseCase: DeleteFileUseCase,
        private val getCourseWorkUseCase: GetCourseWorkUseCase,
        private val getUrlMetadataUseCase: GetUrlMetadataUseCase,
        private val updateCourseWorkUseCaseUseCase: UpdateCourseWorkUseCase,
        private val uploadFileUseCase: UploadFileUseCase,
        private val validateTextField: ValidateTextField,
    ) : ViewModel() {
        var uiState by mutableStateOf(CreateClassworkUiState())
            private set

        private val resultChannel = Channel<String>()
        val createClassworkResults = resultChannel.receiveAsFlow()

        private val courseId: String =
            checkNotNull(savedStateHandle[Routes.Args.CREATE_CLASSWORK_COURSE_ID])
        private val courseWorkId: String =
            checkNotNull(savedStateHandle[Routes.Args.CREATE_CLASSWORK_ID])
        private val workType: String = checkNotNull(savedStateHandle[Routes.Args.CREATE_CLASSWORK_TYPE])
        private val courseWork = mutableStateOf(CourseWork())
        private var getUrlMetadataJob: Job? = null

        init {
            uiState =
                uiState.copy(workType = enumValueOf(workType))
            getCurrentUserUseCase().map { user ->
                if (user != null) {
                    uiState = uiState.copy(userId = user.uid)
                }
            }.launchIn(viewModelScope)
            if (courseWorkId != "null") {
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

                is CreateClassworkUiEvent.OnDueDateValueChange -> {
                    uiState = uiState.copy(dueDate = event.dueDate)
                }

                is CreateClassworkUiEvent.OnFilePicked -> {
                    uploadFile(event.uri, event.fileUtils)
                }

                is CreateClassworkUiEvent.OnOpenAddLinkDialogChange -> {
                    uiState = uiState.copy(openAddLinkDialog = event.openDialog)
                }

                is CreateClassworkUiEvent.OnOpenDatePickerDialogChange -> {
                    uiState = uiState.copy(openDatePickerDialog = event.openDialog)
                }

                is CreateClassworkUiEvent.OnOpenPointsDialogChange -> {
                    uiState = uiState.copy(openPointsDialog = event.openDialog)
                }

                is CreateClassworkUiEvent.OnOpenTimePickerDialogChange -> {
                    uiState = uiState.copy(openTimePickerDialog = event.openDialog)
                }

                is CreateClassworkUiEvent.OnPointsValueChange -> {
                    uiState = uiState.copy(points = event.points)
                }

                is CreateClassworkUiEvent.OnQuestionTypeDropdownExpandedChange -> {
                    uiState = uiState.copy(questionTypeDropdownExpanded = event.expanded)
                }

                is CreateClassworkUiEvent.OnQuestionTypeSelectionOptionValueChange -> {
                    uiState =
                        uiState.copy(
                            questionTypeDropdownExpanded = false,
                            questionTypeSelectionOption = event.selectionOption,
                        )
                }

                is CreateClassworkUiEvent.OnRemoveAttachment -> {
                    val attachment = uiState.attachments[event.position]
                    when {
                        attachment.driveFile != null -> {
                            deleteFile(event.position)
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
                    uiState = uiState.copy(showAddAttachmentBottomSheet = event.showBottomSheet)
                }

                is CreateClassworkUiEvent.OnTitleValueChange -> {
                    uiState =
                        uiState.copy(
                            title = event.title,
                            titleError = null,
                        )
                }

                is CreateClassworkUiEvent.OnWorkTypeValueChange -> {
                    // Empty choices when change workType
                    uiState =
                        uiState.copy(
                            workType = event.workType,
                            choices = mutableStateListOf("Option 1"),
                        )
                }

                CreateClassworkUiEvent.CreateCourseWork -> {
                    if (courseWorkId != "null") {
                        updateCourseWork(courseWorkId)
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
            val calendar = uiState.dueDate
            var dueDate: DueDate? = null
            var dueTime: DueTime? = null
            if (calendar != null) {
                dueTime =
                    DueTime(
                        hours = calendar.get(Calendar.HOUR_OF_DAY),
                        minutes = calendar.get(Calendar.MINUTE),
                        seconds = calendar.get(Calendar.SECOND),
                    )
                dueDate =
                    DueDate(
                        day = calendar.get(Calendar.DAY_OF_MONTH),
                        month = calendar.get(Calendar.MONTH),
                        year = calendar.get(Calendar.YEAR),
                    )
            }

            courseWork.value =
                courseWork.value.copy(
                    description = uiState.description.ifEmpty { null },
                    dueTime = dueTime,
                    dueDate = dueDate,
                    materials = uiState.attachments,
                    maxPoints = maxPoints,
                    multipleChoiceQuestion = multipleChoiceQuestion,
                    title = title,
                    workType = uiState.workType,
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
                        if (courseWorkResponse != null) {
                            uiState = uiState.copy(openProgressDialog = false)
                            resultChannel.send(courseWorkResponse.id.orEmpty())
                        } else {
                            uiState =
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
                            val calendar = Calendar.getInstance()
                            val dueDate = courseWorkResponse.dueDate
                            val dueTime = courseWorkResponse.dueTime
                            if (dueDate != null) {
                                calendar.set(
                                    dueDate.year ?: 0,
                                    dueDate.month ?: 0,
                                    dueDate.day ?: 0,
                                    dueTime?.hours ?: 0,
                                    dueTime?.minutes ?: 0,
                                    dueTime?.seconds ?: 0,
                                )
                            }

                            uiState =
                                uiState.copy(
                                    description = courseWorkResponse.description.orEmpty(),
                                    dueDate = calendar,
                                    isLoading = false,
                                    points = courseWorkResponse.maxPoints?.toString(),
                                    title = courseWorkResponse.title.orEmpty(),
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

        private fun updateCourseWork(id: String) {
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
            val calendar = uiState.dueDate
            var dueDate: DueDate? = null
            var dueTime: DueTime? = null
            if (calendar != null) {
                dueTime =
                    DueTime(
                        hours = calendar.get(Calendar.HOUR_OF_DAY),
                        minutes = calendar.get(Calendar.MINUTE),
                        seconds = calendar.get(Calendar.SECOND),
                    )
                dueDate =
                    DueDate(
                        day = calendar.get(Calendar.DAY_OF_MONTH),
                        month = calendar.get(Calendar.MONTH),
                        year = calendar.get(Calendar.YEAR),
                    )
            }

            courseWork.value =
                courseWork.value.copy(
                    description = uiState.description.ifEmpty { null },
                    dueTime = dueTime,
                    dueDate = dueDate,
                    materials = uiState.attachments,
                    maxPoints = maxPoints,
                    multipleChoiceQuestion = multipleChoiceQuestion,
                    title = title,
                    workType = uiState.workType,
                )

            updateCourseWorkUseCaseUseCase(
                courseId,
                id,
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
                        if (courseWorkResponse != null) {
                            uiState = uiState.copy(openProgressDialog = false)
                            resultChannel.send(courseWorkResponse.id.orEmpty())
                        } else {
                            uiState =
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
            fileUtils: FileUtils,
        ) {
            val fileExtension = fileUtils.getFileExtension(uri)
            val fileName = fileUtils.getFileName(uri) ?: "${uri.lastPathSegment}.$fileExtension"
            val filePath =
                "${FirebaseConstants.Storage.COURSE_STORAGE_PATH}/$courseId/courseWork/${courseWork.value.id}/$fileName"

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
                                        title = fileName,
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

        private fun deleteFile(position: Int) {
            val fileName = uiState.attachments[position].driveFile?.title
            val filePath =
                "${FirebaseConstants.Storage.COURSE_STORAGE_PATH}/$courseId/courseWork/${courseWork.value.id}/$fileName"

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
