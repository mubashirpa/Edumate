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
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.R.string as Strings
import edumate.app.core.FirebaseConstants
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.course_work.*
import edumate.app.domain.usecase.GetUrlMetadataUseCase
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.course_work.CreateCourseWorkUseCase
import edumate.app.domain.usecase.storage.UploadFileUseCase
import edumate.app.domain.usecase.validation.ValidateTextField
import edumate.app.navigation.Routes
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel
class CreateClassworkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUrlMetadataUseCase: GetUrlMetadataUseCase,
    private val createCourseWorkUseCase: CreateCourseWorkUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val validateTextField: ValidateTextField
) : ViewModel() {

    var uiState by mutableStateOf(CreateClassworkUiState())
        private set

    private val resultChannel = Channel<String>()
    val createClassworkResults = resultChannel.receiveAsFlow()

    private val courseWork = mutableStateOf(CourseWork())
    private var urlUseCaseJob: Job? = null
    private val courseId: String? = try {
        checkNotNull(savedStateHandle[Routes.Args.CREATE_CLASSWORK_COURSE_ID])
    } catch (e: IllegalStateException) {
        null
    }
    private val type: String = try {
        checkNotNull(savedStateHandle[Routes.Args.CREATE_CLASSWORK_TYPE])
    } catch (e: IllegalStateException) {
        "${CourseWorkType.MATERIAL}"
    }
    private var currentUser: FirebaseUser? = null

    init {
        val workType = when (type) {
            "${CourseWorkType.ASSIGNMENT}" -> CourseWorkType.ASSIGNMENT
            "${CourseWorkType.SHORT_ANSWER_QUESTION}" -> CourseWorkType.SHORT_ANSWER_QUESTION
            "${CourseWorkType.MULTIPLE_CHOICE_QUESTION}" -> CourseWorkType.MULTIPLE_CHOICE_QUESTION
            else -> CourseWorkType.MATERIAL
        }
        uiState = uiState.copy(workType = workType)
        getCurrentUserUseCase().map { user ->
            currentUser = user
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: CreateClassworkUiEvent) {
        when (event) {
            is CreateClassworkUiEvent.OnAddLinkAttachment -> {
                val link = Link(
                    url = event.link,
                    title = event.link
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
            is CreateClassworkUiEvent.OnGetContent -> {
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
                uiState = uiState.copy(
                    title = event.title,
                    titleError = null
                )
            }
            is CreateClassworkUiEvent.OnRemoveAttachment -> {
                // Stop urlUseCaseJob to avoid conflict
                urlUseCaseJob?.cancel()
                uiState.attachments.removeAt(event.position)
            }
            is CreateClassworkUiEvent.OnWorkTypeChange -> {
                // Empty choices when change workType
                uiState = uiState.copy(
                    workType = event.workType,
                    choices = mutableStateListOf("Option 1")
                )
            }
            CreateClassworkUiEvent.CreateClasswork -> {
                createClasswork()
            }
            CreateClassworkUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun createClasswork() {
        val title = uiState.title
        val titleResult = validateTextField.execute(title)
        val maxPoints = try {
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

        if (!titleResult.successful) {
            uiState = uiState.copy(titleError = UiText.StringResource(Strings.missing_title))
            return
        }

        if (courseId == null || currentUser == null) {
            uiState = uiState.copy(userMessage = UiText.StringResource(Strings.error_unknown))
            return
        }

        courseWork.value = courseWork.value.copy(
            courseId = courseId,
            title = uiState.title,
            description = uiState.description.ifEmpty { null },
            materials = uiState.attachments,
            state = CourseWorkState.PUBLISHED,
            dueTime = uiState.dueDate,
            maxPoints = maxPoints,
            workType = uiState.workType,
            creatorUserId = currentUser!!.uid,
            multipleChoiceQuestion = multipleChoiceQuestion
        )

        createCourseWorkUseCase(courseWork.value).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }
                is Resource.Success -> {
                    uiState = uiState.copy(openProgressDialog = false)
                    resultChannel.send(resource.data.orEmpty())
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

        uploadFileUseCase(
            uri,
            "${FirebaseConstants.Storage.COURSE_STORAGE_PATH}/$courseId/$fileName"
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }
                is Resource.Success -> {
                    val file = File(
                        url = resource.data.toString(),
                        title = fileName,
                        type = mimeType
                    )
                    uiState = uiState.copy(openProgressDialog = false)
                    uiState.attachments.add(Material(file = file))
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

    private fun fetchUrlMetadata(url: String) {
        val position = uiState.attachments.lastIndex
        urlUseCaseJob = getUrlMetadataUseCase(url).onEach { resource ->
            if (resource is Resource.Success) {
                uiState.attachments[position] = Material(link = resource.data)
            }
        }.launchIn(viewModelScope)
    }
}