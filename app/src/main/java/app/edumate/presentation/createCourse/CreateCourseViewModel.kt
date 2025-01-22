package app.edumate.presentation.createCourse

import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.usecase.courses.CreateCourseUseCase
import app.edumate.domain.usecase.courses.GetCourseUseCase
import app.edumate.domain.usecase.courses.UpdateCourseUseCase
import app.edumate.domain.usecase.validation.ValidateTextField
import app.edumate.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CreateCourseViewModel(
    savedStateHandle: SavedStateHandle,
    private val createCourseUseCase: CreateCourseUseCase,
    private val getCourseUseCase: GetCourseUseCase,
    private val updateCourseUseCase: UpdateCourseUseCase,
    private val validateTextField: ValidateTextField,
) : ViewModel() {
    var uiState by mutableStateOf(CreateCourseUiState())
        private set

    private val createCourse = savedStateHandle.toRoute<Screen.CreateCourse>()

    init {
        createCourse.courseId?.let(::getCourse)
    }

    fun onEvent(event: CreateCourseUiEvent) {
        when (event) {
            CreateCourseUiEvent.CreateCourse -> {
                val name =
                    uiState.name.text
                        .toString()
                        .trim()
                val room =
                    uiState.room.text
                        .toString()
                        .trim()
                        .ifEmpty { null }
                val section =
                    uiState.section.text
                        .toString()
                        .trim()
                        .ifEmpty { null }
                val subject =
                    uiState.subject.text
                        .toString()
                        .trim()
                        .ifEmpty { null }

                if (createCourse.courseId == null) {
                    createCourse(
                        name = name,
                        room = room,
                        section = section,
                        subject = subject,
                    )
                } else {
                    updateCourse(
                        id = createCourse.courseId,
                        name = name,
                        room = room,
                        section = section,
                        subject = subject,
                    )
                }
            }

            CreateCourseUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun createCourse(
        name: String,
        room: String?,
        section: String?,
        subject: String?,
    ) {
        val nameResult = validateTextField.execute(name)
        if (!nameResult.successful) {
            uiState = uiState.copy(nameError = UiText.StringResource(R.string.enter_a_class_name))
            return
        }
        uiState = uiState.copy(nameError = null)

        createCourseUseCase(
            name = name,
            room = room,
            section = section,
            subject = subject,
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
                    val courseResponse = result.data
                    uiState =
                        if (courseResponse != null) {
                            uiState.copy(
                                newCourseId = courseResponse.id,
                                openProgressDialog = false,
                            )
                        } else {
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = UiText.StringResource(R.string.error_unexpected),
                            )
                        }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getCourse(id: String) {
        getCourseUseCase(id)
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
                        val courseResponse = result.data
                        if (courseResponse != null) {
                            uiState.name.setTextAndPlaceCursorAtEnd(courseResponse.name.orEmpty())
                            uiState.room.setTextAndPlaceCursorAtEnd(courseResponse.room.orEmpty())
                            uiState.section.setTextAndPlaceCursorAtEnd(courseResponse.section.orEmpty())
                            uiState.subject.setTextAndPlaceCursorAtEnd(courseResponse.subject.orEmpty())

                            uiState = uiState.copy(isLoading = false)
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

    private fun updateCourse(
        id: String,
        name: String,
        room: String?,
        section: String?,
        subject: String?,
    ) {
        val nameResult = validateTextField.execute(name)
        if (!nameResult.successful) {
            uiState = uiState.copy(nameError = UiText.StringResource(R.string.enter_a_class_name))
            return
        }
        uiState = uiState.copy(nameError = null)

        updateCourseUseCase(id, name, room, section, subject)
            .onEach { result ->
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
                        val courseResponse = result.data
                        uiState =
                            if (courseResponse != null) {
                                uiState.copy(
                                    newCourseId = courseResponse.id,
                                    openProgressDialog = false,
                                )
                            } else {
                                uiState.copy(
                                    openProgressDialog = false,
                                    userMessage = UiText.StringResource(R.string.error_unexpected),
                                )
                            }
                    }
                }
            }.launchIn(viewModelScope)
    }
}
