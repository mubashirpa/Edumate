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

        private val courseId: String? =
            try {
                checkNotNull(savedStateHandle[Routes.Args.CREATE_CLASS_COURSE_ID])
            } catch (e: IllegalStateException) {
                null
            }
        val course = mutableStateOf(Course())

        init {
            if (courseId != null) {
                getCourse(courseId)
            }
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

                CreateClassUiEvent.OnCreateClick -> {
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
            getCourseUseCase(id).onEach { resource ->
                when (resource) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                loading = false,
                                userMessage = resource.message,
                            )
                    }

                    is Result.Loading -> {
                        uiState = uiState.copy(loading = true)
                    }

                    is Result.Success -> {
                        val updatedCourse = resource.data
                        if (updatedCourse != null) {
                            course.value = updatedCourse
                            uiState =
                                uiState.copy(
                                    loading = false,
                                    name = updatedCourse.name.orEmpty(),
                                    room = updatedCourse.room.orEmpty(),
                                    section = updatedCourse.section.orEmpty(),
                                    subject = updatedCourse.subject.orEmpty(),
                                )
                        } else {
                            uiState =
                                uiState.copy(
                                    loading = false,
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

            updateCourseUseCase(id, course.value).onEach { resource ->
                when (resource) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = resource.message,
                            )
                    }

                    is Result.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }

                    is Result.Success -> {
                        val courseId = resource.data
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
