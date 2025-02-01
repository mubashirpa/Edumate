package app.edumate.presentation.createCourseWork

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import app.edumate.domain.usecase.courseWork.CreateAssignmentUseCase
import app.edumate.domain.usecase.courseWork.CreateMaterialUseCase
import app.edumate.domain.usecase.courseWork.CreateQuestionUseCase
import app.edumate.domain.usecase.courseWork.GetCourseWorkUseCase
import app.edumate.domain.usecase.courseWork.UpdateCourseWorkUseCase
import app.edumate.domain.usecase.storage.DeleteFileUseCase
import app.edumate.domain.usecase.storage.UploadFileUseCase
import app.edumate.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File
import java.util.UUID

class CreateCourseWorkViewModel(
    savedStateHandle: SavedStateHandle,
    private val createAssignmentUseCase: CreateAssignmentUseCase,
    private val createMaterialUseCase: CreateMaterialUseCase,
    private val createQuestionUseCase: CreateQuestionUseCase,
    private val getCourseWorkUseCase: GetCourseWorkUseCase,
    private val updateCourseWorkUseCase: UpdateCourseWorkUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val deleteFileUseCase: DeleteFileUseCase,
    private val getUrlMetadataUseCase: GetUrlMetadataUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(CreateCourseWorkUiState())
        private set

    private val args = savedStateHandle.toRoute<Screen.CreateCourseWork>()
    private val courseWorkId = args.id ?: UUID.randomUUID().toString()

    init {
        uiState = uiState.copy(workType = args.workType)
        args.id?.let(::getCourseWork)
    }

    fun onEvent(event: CreateCourseWorkUiEvent) {
        when (event) {
            CreateCourseWorkUiEvent.CreateCourseWork -> {
                val title =
                    uiState.title.text
                        .toString()
                        .trim()
                val description =
                    uiState.description.text
                        .toString()
                        .trim()
                val maxPoints =
                    try {
                        uiState.points?.toInt()
                    } catch (_: NumberFormatException) {
                        null
                    }

                if (args.id == null) {
                    createCourseWork(
                        courseId = args.courseId,
                        id = courseWorkId,
                        title = title,
                        description = description,
                        choices = uiState.choices,
                        materials = uiState.attachments,
                        maxPoints = maxPoints,
                        dueTime = uiState.dueTime?.toString()?.plus("+05:30"),
                        workType = uiState.workType!!,
                    )
                } else {
                    updateCourseWork(
                        id = courseWorkId,
                        title = title,
                        description = description,
                        choices = uiState.choices,
                        materials = uiState.attachments,
                        maxPoints = maxPoints,
                        dueTime = uiState.dueTime?.toString()?.plus("+05:30"),
                    )
                }
            }

            is CreateCourseWorkUiEvent.OnAddLinkAttachment -> {
                getUrlMetadata(event.link)
            }

            is CreateCourseWorkUiEvent.OnDueTimeValueChange -> {
                uiState = uiState.copy(dueTime = event.dateTime)
            }

            is CreateCourseWorkUiEvent.OnFilePicked -> {
                uploadFile(
                    courseId = args.courseId,
                    id = courseWorkId,
                    title = event.title,
                    file = event.file,
                )
            }

            is CreateCourseWorkUiEvent.OnOpenAddLinkDialogChange -> {
                uiState = uiState.copy(openAddLinkDialog = event.open)
            }

            is CreateCourseWorkUiEvent.OnOpenDatePickerDialogChange -> {
                uiState = uiState.copy(openDatePickerDialog = event.open)
            }

            is CreateCourseWorkUiEvent.OnOpenPointsDialogChange -> {
                uiState = uiState.copy(openPointsDialog = event.open)
            }

            is CreateCourseWorkUiEvent.OnOpenTimePickerDialogChange -> {
                uiState = uiState.copy(openTimePickerDialog = event.open)
            }

            is CreateCourseWorkUiEvent.OnPointsValueChange -> {
                val points =
                    try {
                        event.points?.toInt()
                    } catch (_: NumberFormatException) {
                        null
                    }.takeIf { it != null && it > 0 }

                uiState = uiState.copy(points = points)
            }

            is CreateCourseWorkUiEvent.OnQuestionTypeDropdownExpandedChange -> {
                uiState = uiState.copy(questionTypeDropdownExpanded = event.expanded)
            }

            is CreateCourseWorkUiEvent.OnQuestionTypeValueChange -> {
                val questionType =
                    when (event.selectionOptionIndex) {
                        0 -> CourseWorkType.SHORT_ANSWER_QUESTION
                        else -> CourseWorkType.MULTIPLE_CHOICE_QUESTION
                    }

                // Empty choices when change workType
                uiState =
                    uiState.copy(
                        choices =
                            if (questionType == CourseWorkType.MULTIPLE_CHOICE_QUESTION) {
                                mutableStateListOf("Option 1")
                            } else {
                                mutableStateListOf()
                            },
                        questionTypeDropdownExpanded = false,
                        questionTypeSelectionOptionIndex = event.selectionOptionIndex,
                        workType = questionType,
                    )
            }

            is CreateCourseWorkUiEvent.OnRemoveAttachment -> {
                val attachment = uiState.attachments[event.position]
                when {
                    attachment.driveFile != null -> {
                        deleteFile(
                            courseId = args.courseId,
                            id = courseWorkId,
                            material = attachment,
                        )
                    }

                    attachment.link != null -> {
                        uiState.attachments.removeAt(event.position)
                    }
                }
            }

            is CreateCourseWorkUiEvent.OnShowAddAttachmentBottomSheetChange -> {
                uiState = uiState.copy(showAddAttachmentBottomSheet = event.show)
            }

            CreateCourseWorkUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun uploadFile(
        courseId: String,
        id: String,
        title: String,
        file: File,
    ) {
        uploadFileUseCase(
            bucketId = Supabase.Storage.MATERIALS_BUCKET_ID,
            path = "$courseId/coursework/$id/$title",
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
                                title = title,
                            )
                        uiState.attachments.add(Material(driveFile = driveFile))
                        uiState = uiState.copy(uploadProgress = null)
                    } else {
                        uiState = uiState.copy(uploadProgress = state.progress)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteFile(
        courseId: String,
        id: String,
        material: Material,
    ) {
        val title = material.driveFile?.title ?: return

        deleteFileUseCase(
            bucketId = Supabase.Storage.MATERIALS_BUCKET_ID,
            paths = listOf("$courseId/coursework/$id/$title"),
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
                    uiState.attachments.remove(material)
                    uiState = uiState.copy(openProgressDialog = false)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getUrlMetadata(url: String) {
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
                        uiState.attachments.add(Material(link = link))
                        uiState = uiState.copy(openProgressDialog = false)
                    }

                    is Result.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }

                    is Result.Success -> {
                        val link = result.data!!
                        uiState.attachments.add(Material(link = link))
                        uiState = uiState.copy(openProgressDialog = false)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getCourseWork(id: String) {
        getCourseWorkUseCase(id)
            .onEach { result ->
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
                            courseWorkResponse.materials?.let { attachments ->
                                uiState.attachments.addAll(attachments)
                            }
                            val choices =
                                courseWorkResponse.multipleChoiceQuestion?.choices
                            if (!choices.isNullOrEmpty()) {
                                uiState.choices.clear()
                                uiState.choices.addAll(choices)
                            }
                            courseWorkResponse.description?.let { description ->
                                uiState.description.setTextAndPlaceCursorAtEnd(description)
                            }
                            val dueTime =
                                courseWorkResponse.dueTime?.let { input ->
                                    Instant
                                        .parse(input)
                                        .toLocalDateTime(TimeZone.currentSystemDefault())
                                }
                            courseWorkResponse.title?.let { title ->
                                uiState.title.setTextAndPlaceCursorAtEnd(title)
                            }
                            val questionTypeSelectionOptionIndex =
                                courseWorkResponse.workType?.let { workType ->
                                    when (courseWorkResponse.workType) {
                                        CourseWorkType.MULTIPLE_CHOICE_QUESTION -> 1
                                        CourseWorkType.SHORT_ANSWER_QUESTION -> 0
                                        else -> null
                                    }
                                }

                            uiState =
                                uiState.copy(
                                    dueTime = dueTime,
                                    isLoading = false,
                                    points = courseWorkResponse.maxPoints,
                                    questionTypeSelectionOptionIndex = questionTypeSelectionOptionIndex,
                                    workType = courseWorkResponse.workType,
                                )
                        } else {
                            uiState =
                                uiState.copy(
                                    isLoading = false,
                                    userMessage = UiText.StringResource(R.string.error_unexpected),
                                )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun createCourseWork(
        courseId: String,
        id: String,
        title: String,
        description: String?,
        choices: List<String>?,
        materials: List<Material>?,
        maxPoints: Int?,
        dueTime: String?,
        workType: CourseWorkType,
    ) {
        when (workType) {
            CourseWorkType.ASSIGNMENT -> {
                createAssignmentUseCase(
                    courseId = courseId,
                    title = title,
                    description = description,
                    materials = materials,
                    maxPoints = maxPoints,
                    dueTime = dueTime,
                    id = id,
                )
            }

            CourseWorkType.MATERIAL -> {
                createMaterialUseCase(
                    courseId = courseId,
                    title = title,
                    description = description,
                    materials = materials,
                    id = id,
                )
            }

            else -> {
                createQuestionUseCase(
                    courseId = courseId,
                    title = title,
                    description = description,
                    choices = choices,
                    materials = materials,
                    maxPoints = maxPoints,
                    dueTime = dueTime,
                    workType = workType,
                    id = id,
                )
            }
        }.onEach { result ->
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
                    uiState =
                        uiState.copy(
                            isCreateCourseWorkSuccess = true,
                            openProgressDialog = false,
                        )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun updateCourseWork(
        id: String,
        title: String,
        description: String?,
        choices: List<String>?,
        materials: List<Material>?,
        maxPoints: Int?,
        dueTime: String?,
    ) {
        updateCourseWorkUseCase(
            id = id,
            title = title,
            description = description,
            choices = choices,
            materials = materials,
            maxPoints = maxPoints,
            dueTime = dueTime,
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
                    uiState =
                        uiState.copy(
                            isCreateCourseWorkSuccess = true,
                            openProgressDialog = false,
                        )
                }
            }
        }.launchIn(viewModelScope)
    }
}
