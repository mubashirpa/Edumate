package edumate.app.presentation.joinClass

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.students.AddStudentUseCase
import edumate.app.domain.usecase.teachers.AddTeacherUseCase
import edumate.app.domain.usecase.validation.ValidateTextField
import edumate.app.presentation.class_details.UserType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import edumate.app.R.string as Strings

@HiltViewModel
class JoinClassViewModel
    @Inject
    constructor(
        getCurrentUserUseCase: GetCurrentUserUseCase,
        private val addStudentUseCase: AddStudentUseCase,
        private val addTeacherUseCase: AddTeacherUseCase,
        private val validateTextField: ValidateTextField,
    ) : ViewModel() {
        var uiState by mutableStateOf(JoinClassUiState())
            private set

        private val resultChannel = Channel<String>()
        val joinClassResults = resultChannel.receiveAsFlow()

        init {
            getCurrentUserUseCase().map { user ->
                uiState = uiState.copy(currentUser = user)
            }.launchIn(viewModelScope)
        }

        fun onEvent(event: JoinClassUiEvent) {
            when (event) {
                is JoinClassUiEvent.OnClassCodeChange -> {
                    uiState =
                        uiState.copy(
                            classCode = event.classCode,
                            classCodeError = null,
                        )
                }

                is JoinClassUiEvent.OnOpenUserTypeBottomSheetChange -> {
                    uiState = uiState.copy(openUserTypeBottomSheet = event.open)
                }

                is JoinClassUiEvent.OnUserTypeChange -> {
                    uiState = uiState.copy(userType = event.userType)
                }

                JoinClassUiEvent.JoinClass -> {
                    if (uiState.userType == UserType.STUDENT) {
                        joinClassAsStudent()
                    } else {
                        joinClassAsTeacher()
                    }
                }

                JoinClassUiEvent.UserMessageShown -> {
                    uiState = uiState.copy(userMessage = null)
                }
            }
        }

        private fun joinClassAsStudent() {
            val classCode =
                if (uiState.classCode.startsWith("https://edumateapp.web.app/c/details?cid=")) {
                    uiState.classCode.replace("https://edumateapp.web.app/c/details?cid=", "")
                } else {
                    uiState.classCode
                }
            val classCodeResult = validateTextField.execute(classCode)

            if (!classCodeResult.successful) {
                uiState = uiState.copy(classCodeError = classCodeResult.error)
                return
            }

            addStudentUseCase(classCode).onEach { result ->
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
                        uiState = uiState.copy(openProgressDialog = false)
                        resultChannel.send(classCode)
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun joinClassAsTeacher() {
            val classCode =
                if (uiState.classCode.startsWith("https://edumateapp.web.app/c/details?cid=")) {
                    uiState.classCode.replace("https://edumateapp.web.app/c/details?cid=", "")
                } else {
                    uiState.classCode
                }
            val classCodeResult = validateTextField.execute(classCode)

            if (!classCodeResult.successful) {
                uiState = uiState.copy(classCodeError = classCodeResult.error)
                return
            }

            addTeacherUseCase(classCode).onEach { result ->
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
                        uiState = uiState.copy(openProgressDialog = false)
                        resultChannel.send(classCode)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
