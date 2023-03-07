package edumate.app.presentation.create_class

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import edumate.app.domain.usecase.validation.ValidateTextField
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel
class CreateClassViewModel @Inject constructor(
    private val createCourseUseCase: CreateCourseUseCase,
    private val deleteCourseUseCase: DeleteCourseUseCase,
    private val validateTextField: ValidateTextField,
    getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    var uiState by mutableStateOf(CreateClassUiState())
        private set

    private val resultChannel = Channel<String>()
    val createClassResults = resultChannel.receiveAsFlow()

    private val course = mutableStateOf(Course())

    init {
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
                createClass()
            }
            is CreateClassUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun createClass() {
        val nameResult = validateTextField.execute(uiState.name)
        uiState = uiState.copy(nameError = nameResult.error)

        if (!nameResult.successful) return

        if (uiState.currentUser == null) {
            uiState = uiState.copy(userMessage = UiText.StringResource(Strings.error_unexpected))
            return
        }

        createCourseUseCase(course.value).onEach { resource ->
            val courseId = resource.data
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
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
}