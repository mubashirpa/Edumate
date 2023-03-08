package edumate.app.presentation.create_class

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.model.Course
import edumate.app.domain.model.CourseState
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.courses.CreateCourseUseCase
import edumate.app.domain.usecase.courses.DeleteCourseUseCase
import edumate.app.domain.usecase.courses.GetCourseUseCase
import edumate.app.domain.usecase.courses.UpdateCourseUseCase
import edumate.app.domain.usecase.validation.ValidateTextField
import edumate.app.navigation.Routes
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel
class CreateClassViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createCourseUseCase: CreateCourseUseCase,
    private val updateCourseUseCase: UpdateCourseUseCase,
    private val getCourseUseCase: GetCourseUseCase,
    private val deleteCourseUseCase: DeleteCourseUseCase,
    private val validateTextField: ValidateTextField,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    var uiState by mutableStateOf(CreateClassUiState())
        private set

    private val resultChannel = Channel<String>()
    val createClassResults = resultChannel.receiveAsFlow()

    private val id: String? = try {
        checkNotNull(savedStateHandle[Routes.Args.CREATE_CLASS_COURSE_ID])
    } catch (e: IllegalStateException) {
        null
    }

    private val course = mutableStateOf(Course())

    init {
        if (id == null) {
            loadUser()
        } else {
            fetchCourse(id)
        }
    }

    fun onEvent(event: CreateClassUiEvent) {
        when (event) {
            is CreateClassUiEvent.NameChanged -> {
                uiState = uiState.copy(
                    name = event.name,
                    nameError = null,
                    isFabExpanded = event.name.isNotBlank()
                )
                course.value = course.value.copy(name = event.name)
            }
            is CreateClassUiEvent.SectionChanged -> {
                uiState = uiState.copy(section = event.section)
                course.value = course.value.copy(section = event.section)
            }
            is CreateClassUiEvent.RoomChanged -> {
                uiState = uiState.copy(room = event.room)
                course.value = course.value.copy(room = event.room)
            }
            is CreateClassUiEvent.SubjectChanged -> {
                uiState = uiState.copy(subject = event.subject)
                course.value = course.value.copy(subject = event.subject)
            }
            is CreateClassUiEvent.OnCreateClick -> {
                if (id == null) {
                    createCourse()
                } else {
                    updateCourse()
                }
            }
            is CreateClassUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun createCourse() {
        val nameResult = validateTextField.execute(uiState.name)
        uiState = uiState.copy(nameError = nameResult.error)

        if (!nameResult.successful) return

        // This is used here because loadUser()
        if (uiState.currentUser == null) {
            uiState = uiState.copy(userMessage = UiText.StringResource(Strings.error_unexpected))
            return
        }

        createCourseUseCase(course.value).onEach { resource ->
            val courseId = resource.data
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(
                        progressDialogText = UiText.StringResource(Strings.creating_class),
                        openProgressDialog = true
                    )
                }
                is Resource.Success -> {
                    if (courseId != null) {
                        uiState = uiState.copy(openProgressDialog = false)
                        resultChannel.send(courseId)
                    } else {
                        uiState = uiState.copy(
                            openProgressDialog = false,
                            userMessage = UiText.StringResource(Strings.error_unknown)
                        )
                    }
                }
                is Resource.Error -> {
                    if (courseId != null) {
                        deleteCourseUseCase(courseId).launchIn(viewModelScope)
                    }
                    uiState = uiState.copy(
                        openProgressDialog = false,
                        userMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun updateCourse() {
        val nameResult = validateTextField.execute(uiState.name)
        uiState = uiState.copy(nameError = nameResult.error)

        if (!nameResult.successful) return

        updateCourseUseCase(course.value).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(
                        progressDialogText = UiText.StringResource(Strings.updating_class),
                        openProgressDialog = true
                    )
                }
                is Resource.Success -> {
                    uiState = uiState.copy(openProgressDialog = false)
                    resultChannel.send(resource.data!!)
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

    private fun fetchCourse(courseId: String) {
        getCourseUseCase(courseId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(loading = true)
                }
                is Resource.Success -> {
                    val data = resource.data
                    if (data != null) {
                        course.value = data
                        uiState = uiState.copy(
                            loading = false,
                            name = data.name,
                            section = data.section.orEmpty(),
                            room = data.room.orEmpty(),
                            subject = data.subject.orEmpty(),
                            isFabExpanded = true
                        )
                    } else {
                        uiState = uiState.copy(
                            loading = false,
                            error = resource.message
                        )
                    }
                }
                is Resource.Error -> {
                    uiState = uiState.copy(
                        loading = false,
                        error = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadUser() {
        getCurrentUserUseCase().map { user ->
            uiState = uiState.copy(currentUser = user)
            if (user != null) {
                course.value = course.value.copy(
                    courseState = CourseState.ACTIVE,
                    ownerId = user.uid,
                    teachers = arrayListOf(user.uid)
                )
            }
        }.launchIn(viewModelScope)
    }
}