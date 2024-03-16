package edumate.app.presentation.people

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.usecase.authentication.GetCurrentUserIdUseCase
import edumate.app.domain.usecase.classroom.students.DeleteStudentUseCase
import edumate.app.domain.usecase.classroom.students.ListStudentsUseCase
import edumate.app.domain.usecase.classroom.teachers.DeleteTeacherUseCase
import edumate.app.domain.usecase.classroom.teachers.ListTeachersUseCase
import edumate.app.navigation.Routes
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import edumate.app.R.string as Strings

@HiltViewModel
class PeopleViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
        private val deleteStudentUseCase: DeleteStudentUseCase,
        private val deleteTeacherUseCase: DeleteTeacherUseCase,
        private val listStudentsUseCase: ListStudentsUseCase,
        private val listTeachersUseCase: ListTeachersUseCase,
    ) : ViewModel() {
        var uiState by mutableStateOf(PeopleUiState())
            private set

        private val courseId: String = checkNotNull(savedStateHandle[Routes.Args.PEOPLE_COURSE_ID])
        private var listTeachersJob: Job? = null
        private var listStudentsJob: Job? = null

        init {
            uiState = uiState.copy(userId = getCurrentUserIdUseCase())
            listStudents(false)
            listTeachers(false)
        }

        fun onEvent(event: PeopleUiEvent) {
            when (event) {
                is PeopleUiEvent.OnAppBarDropdownExpandedChange -> {
                    uiState = uiState.copy(appBarDropdownExpanded = event.expanded)
                }

                is PeopleUiEvent.OnDeleteStudent -> {
                    deleteStudent(event.userId)
                }

                is PeopleUiEvent.OnDeleteTeacher -> {
                    deleteTeacher(event.userId, false)
                }

                is PeopleUiEvent.OnFilterChange -> {
                    uiState = uiState.copy(filter = event.peopleFilterType)
                }

                is PeopleUiEvent.OnLeaveClass -> {
                    deleteTeacher(event.userId, true)
                }

                is PeopleUiEvent.OnOpenDeleteUserDialogChange -> {
                    uiState = uiState.copy(deleteUserProfile = event.userProfile)
                }

                is PeopleUiEvent.OnOpenLeaveClassDialogChange -> {
                    uiState = uiState.copy(openLeaveClassDialog = event.open)
                }

                is PeopleUiEvent.OnShowInviteBottomSheetChange -> {
                    uiState = uiState.copy(showInviteBottomSheet = event.show)
                }

                is PeopleUiEvent.OnRefresh -> {
                    listStudents(true)
                    listTeachers(true)
                }

                is PeopleUiEvent.OnRetry -> {
                    if (uiState.studentsResult is Result.Error) {
                        listStudents(false)
                    }
                    if (uiState.teachersResult is Result.Error) {
                        listTeachers(false)
                    }
                }

                is PeopleUiEvent.UserMessageShown -> {
                    uiState = uiState.copy(userMessage = null)
                }
            }
        }

        private fun deleteStudent(userId: String) {
            deleteStudentUseCase(courseId, userId).onEach { result ->
                when (result) {
                    is Result.Empty -> {}
                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = UiText.StringResource(Strings.unable_to_remove_student),
                            )
                    }

                    is Result.Loading -> {
                        uiState =
                            uiState.copy(
                                deleteUserProfile = null,
                                openProgressDialog = true,
                            )
                    }

                    is Result.Success -> {
                        uiState = uiState.copy(openProgressDialog = false)
                        listStudents(true)
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun deleteTeacher(
            userId: String,
            isLeaving: Boolean,
        ) {
            // If the user is leaving, exit the page on success; otherwise, refresh teachers
            deleteTeacherUseCase(courseId, userId).onEach { result ->
                when (result) {
                    is Result.Empty -> {}
                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage =
                                    if (isLeaving) {
                                        UiText.StringResource(Strings.unable_to_leave_class)
                                    } else {
                                        UiText.StringResource(Strings.unable_to_remove_teacher)
                                    },
                            )
                    }

                    is Result.Loading -> {
                        uiState =
                            uiState.copy(
                                deleteUserProfile = null,
                                openProgressDialog = true,
                            )
                    }

                    is Result.Success -> {
                        if (isLeaving) {
                            uiState =
                                uiState.copy(
                                    isUserLeaveClass = true,
                                    openProgressDialog = false,
                                )
                        } else {
                            uiState = uiState.copy(openProgressDialog = false)
                            listTeachers(true)
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun listStudents(isRefreshing: Boolean) {
            // Cancel any ongoing listStudentsJob before making a new call.
            listStudentsJob?.cancel()
            listStudentsJob =
                listStudentsUseCase(courseId).onEach { result ->
                    when (result) {
                        is Result.Empty -> {}

                        is Result.Error -> {
                            // The Result.Error state is only used during initial loading and retry attempts.
                            // Otherwise, a snackbar is displayed using the userMessage property.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(
                                        isStudentsRefreshing = false,
                                        userMessage = result.message,
                                    )
                                } else {
                                    uiState.copy(studentsResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with isRefreshing = true.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(isStudentsRefreshing = true)
                                } else {
                                    uiState.copy(studentsResult = result)
                                }
                        }

                        is Result.Success -> {
                            uiState =
                                uiState.copy(
                                    isStudentsRefreshing = false,
                                    studentsResult = result,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
        }

        private fun listTeachers(isRefreshing: Boolean) {
            // Cancel any ongoing listTeachersJob before making a new call.
            listTeachersJob?.cancel()
            listTeachersJob =
                listTeachersUseCase(courseId).onEach { result ->
                    when (result) {
                        is Result.Empty -> {}

                        is Result.Error -> {
                            // The Result.Error state is only used during initial loading and retry attempts.
                            // Otherwise, a snackbar is displayed using the userMessage property.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(
                                        isTeachersRefreshing = false,
                                        userMessage = result.message,
                                    )
                                } else {
                                    uiState.copy(teachersResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with isRefreshing = true.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(isTeachersRefreshing = true)
                                } else {
                                    uiState.copy(teachersResult = result)
                                }
                        }

                        is Result.Success -> {
                            uiState =
                                uiState.copy(
                                    isTeachersRefreshing = false,
                                    teachersResult = result,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
        }
    }
