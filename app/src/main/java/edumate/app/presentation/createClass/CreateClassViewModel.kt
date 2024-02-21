package edumate.app.presentation.createClass

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.model.classroom.courses.Course
import edumate.app.domain.usecase.classroom.courses.CreateCourseUseCase
import edumate.app.domain.usecase.classroom.courses.GetCourseUseCase
import edumate.app.domain.usecase.classroom.courses.UpdateCourseUseCase
import edumate.app.domain.usecase.validation.ValidateTextField
import edumate.app.navigation.Routes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import edumate.app.R.string as Strings

@HiltViewModel
class CreateClassViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val createCourseUseCase: CreateCourseUseCase,
        private val getCourseUseCase: GetCourseUseCase,
        private val updateCourseUseCase: UpdateCourseUseCase,
        private val validateTextField: ValidateTextField,
    ) : ViewModel() {
        var uiState by mutableStateOf(CreateClassUiState())
            private set

        private val resultChannel = Channel<String>()
        val createClassResults = resultChannel.receiveAsFlow()

        private val courseId: String? = savedStateHandle[Routes.Args.CREATE_CLASS_COURSE_ID]
        val course = mutableStateOf(Course())

        init {
            courseId?.let(::getCourse)
        }

        fun onEvent(event: CreateClassUiEvent) {
            when (event) {
                is CreateClassUiEvent.OnNameValueChange -> {
                    uiState =
                        uiState.copy(
                            name = event.name,
                            nameError = null,
                        )
                }

                is CreateClassUiEvent.OnRoomValueChange -> {
                    uiState = uiState.copy(room = event.room)
                }

                is CreateClassUiEvent.OnSectionValueChange -> {
                    uiState = uiState.copy(section = event.section)
                }

                is CreateClassUiEvent.OnSubjectValueChange -> {
                    uiState = uiState.copy(subject = event.subject)
                }

                CreateClassUiEvent.CreateClass -> {
                    if (courseId == null) {
                        createCourse()
                    } else {
                        updateCourse(courseId)
                    }
                }

                CreateClassUiEvent.UserMessageShown -> {
                    uiState = uiState.copy(userMessage = null)
                }
            }
        }

        private fun createCourse() {
            val name = uiState.name
            val nameResult = validateTextField.execute(name)

            if (!nameResult.successful) {
                uiState = uiState.copy(nameError = UiText.StringResource(Strings.enter_a_class_name))
                return
            }

            course.value =
                course.value.copy(
                    name = name,
                    room = uiState.room.ifEmpty { null },
                    section = uiState.section.ifEmpty { null },
                    subject = uiState.subject.ifEmpty { null },
                )

            createCourseUseCase(course.value).onEach { result ->
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
                        val updatedCourse = result.data
                        if (updatedCourse != null) {
                            uiState = uiState.copy(openProgressDialog = false)
                            resultChannel.send(updatedCourse.id.orEmpty())
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

        private fun getCourse(id: String) {
            getCourseUseCase(id).onEach { result ->
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
                            course.value = courseResponse
                            uiState =
                                uiState.copy(
                                    isLoading = false,
                                    name = courseResponse.name.orEmpty(),
                                    room = courseResponse.room.orEmpty(),
                                    section = courseResponse.section.orEmpty(),
                                    subject = courseResponse.subject.orEmpty(),
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

        private fun updateCourse(id: String) {
            val name = uiState.name
            val nameResult = validateTextField.execute(name)

            if (!nameResult.successful) {
                uiState = uiState.copy(nameError = UiText.StringResource(Strings.enter_a_class_name))
                return
            }

            course.value =
                course.value.copy(
                    name = name,
                    room = uiState.room.ifEmpty { null },
                    section = uiState.section.ifEmpty { null },
                    subject = uiState.subject.ifEmpty { null },
                )

            updateCourseUseCase(id, course.value).onEach { result ->
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
                        val courseId = result.data
                        if (courseId != null) {
                            uiState = uiState.copy(openProgressDialog = false)
                            resultChannel.send(courseId)
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
    }
