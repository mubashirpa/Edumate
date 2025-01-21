package app.edumate.presentation.createCourse

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import app.edumate.R
import app.edumate.core.UiText
import app.edumate.domain.usecase.validation.ValidateTextField
import app.edumate.navigation.Screen

class CreateCourseViewModel(
    savedStateHandle: SavedStateHandle,
    private val validateTextField: ValidateTextField,
) : ViewModel() {
    var uiState by mutableStateOf(CreateCourseUiState())
        private set

    private val createCourse = savedStateHandle.toRoute<Screen.CreateCourse>()

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
                        courseId = createCourse.courseId,
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

        // TODO: Create course
    }

    private fun updateCourse(
        courseId: String,
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

        // TODO: Update course
    }
}
