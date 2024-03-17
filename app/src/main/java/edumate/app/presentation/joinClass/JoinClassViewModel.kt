package edumate.app.presentation.joinClass

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.model.classroom.students.Student
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.classroom.students.CreateStudentUseCase
import edumate.app.domain.usecase.validation.ValidateTextField
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import edumate.app.R.string as Strings

@HiltViewModel
class JoinClassViewModel
    @Inject
    constructor(
        getCurrentUserUseCase: GetCurrentUserUseCase,
        private val createStudentUseCase: CreateStudentUseCase,
        private val validateTextField: ValidateTextField,
    ) : ViewModel() {
        var uiState by mutableStateOf(JoinClassUiState())
            private set

        init {
            getCurrentUserUseCase().map { user ->
                uiState = uiState.copy(currentUser = user)
            }.launchIn(viewModelScope)
        }

        fun onEvent(event: JoinClassUiEvent) {
            when (event) {
                is JoinClassUiEvent.OnClassCodeValueChange -> {
                    uiState =
                        uiState.copy(
                            classCode = event.classCode,
                            classCodeError = null,
                        )
                }

                JoinClassUiEvent.JoinClass -> {
                    val classCode =
                        if (uiState.classCode.startsWith("https://edumateapp.web.app/c/details?cid=")) {
                            uiState.classCode.replace("https://edumateapp.web.app/c/details?cid=", "")
                        } else {
                            uiState.classCode
                        }.trim()
                    joinClass(classCode)
                }

                JoinClassUiEvent.UserMessageShown -> {
                    uiState = uiState.copy(userMessage = null)
                }
            }
        }

        private fun joinClass(classCode: String) {
            val classCodeResult = validateTextField.execute(classCode)
            if (!classCodeResult.successful) {
                uiState = uiState.copy(classCodeError = classCodeResult.error)
                return
            }

            val student = Student(userId = uiState.currentUser?.id)

            createStudentUseCase(
                courseId = classCode,
                student = student,
            ).onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = UiText.StringResource(Strings.unable_to_join_class),
                            )
                    }

                    is Result.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }

                    is Result.Success -> {
                        val studentResponse = result.data
                        uiState =
                            if (studentResponse != null) {
                                uiState.copy(
                                    joinClassId = student.courseId.orEmpty(),
                                    openProgressDialog = false,
                                )
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
    }
