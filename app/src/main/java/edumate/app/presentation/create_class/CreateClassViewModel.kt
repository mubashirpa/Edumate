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
    private val initialCourse = mutableStateOf(
        Course(
            name = "",
            section = "",
            room = "",
            subject = ""
        )
    )

    init {
        if (id == null) {
            fetchUserDetails()
        } else {
            fetchCourse(id)
        }
    }

    fun onEvent(event: CreateClassUiEvent) {
        when (event) {
            is CreateClassUiEvent.NameChanged -> {
                uiState = uiState.copy(
                    course = uiState.course.copy(name = event.name),
                    nameError = null,
                    isFabExpanded = initialCourse.value != uiState.course.copy(name = event.name) && event.name.isNotBlank()
                )
            }
            is CreateClassUiEvent.SectionChanged -> {
                uiState = uiState.copy(
                    course = uiState.course.copy(section = event.section),
                    isFabExpanded = initialCourse.value != uiState.course.copy(
                        section = event.section
                    ) && uiState.course.name.isNotBlank()
                )
            }
            is CreateClassUiEvent.RoomChanged -> {
                uiState = uiState.copy(
                    course = uiState.course.copy(room = event.room),
                    isFabExpanded = initialCourse.value != uiState.course.copy(room = event.room) && uiState.course.name.isNotBlank()
                )
            }
            is CreateClassUiEvent.SubjectChanged -> {
                uiState = uiState.copy(
                    course = uiState.course.copy(subject = event.subject),
                    isFabExpanded = initialCourse.value != uiState.course.copy(
                        subject = event.subject
                    ) && uiState.course.name.isNotBlank()
                )
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
        val nameResult = validateTextField.execute(uiState.course.name)
        uiState = uiState.copy(nameError = UiText.StringResource(Strings.enter_class_name))

        if (!nameResult.successful) return

        // Return if currentUser is null
        if (uiState.currentUser == null) {
            uiState = uiState.copy(userMessage = UiText.StringResource(Strings.error_unexpected))
            return
        }

        createCourseUseCase(uiState.course).onEach { resource ->
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
        val nameResult = validateTextField.execute(uiState.course.name)
        uiState = uiState.copy(nameError = UiText.StringResource(Strings.enter_class_name))

        if (!nameResult.successful) return

        updateCourseUseCase(uiState.course).onEach { resource ->
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
                    val course = resource.data
                    if (course != null) {
                        initialCourse.value = course
                        uiState = uiState.copy(
                            loading = false,
                            course = course
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

    private fun fetchUserDetails() {
        getCurrentUserUseCase().map { user ->
            if (user != null) {
                uiState = uiState.copy(
                    course = uiState.course.copy(
                        courseState = CourseState.ACTIVE,
                        ownerId = user.uid,
                        teachers = arrayListOf(user.uid)
                    ),
                    currentUser = user
                )
                initialCourse.value = uiState.course
            }
        }.launchIn(viewModelScope)
    }
}