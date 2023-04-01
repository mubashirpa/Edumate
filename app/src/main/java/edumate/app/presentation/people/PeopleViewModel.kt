package edumate.app.presentation.people

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
import edumate.app.domain.model.User
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.courses.GetPeoplesUseCase
import edumate.app.domain.usecase.students.DeleteStudentUseCase
import edumate.app.domain.usecase.teachers.DeleteTeacherUseCase
import edumate.app.navigation.Routes
import edumate.app.presentation.class_details.UserType
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class PeopleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getPeoplesUseCase: GetPeoplesUseCase,
    private val deleteTeacherUseCase: DeleteTeacherUseCase,
    private val deleteStudentUseCase: DeleteStudentUseCase
) : ViewModel() {

    var uiState by mutableStateOf(PeopleUiState())
        private set

    private val courseId: String? = savedStateHandle[Routes.Args.CLASS_DETAILS_COURSE_ID]
    private var peoples: List<User> = emptyList()

    init {
        getCurrentUserUseCase().map { user ->
            uiState = uiState.copy(currentUser = user)
        }.launchIn(viewModelScope)
        fetchPeoples(false)
    }

    fun onEvent(event: PeopleUiEvent) {
        when (event) {
            is PeopleUiEvent.OnDeletePeople -> {
                when (event.userType) {
                    UserType.STUDENT -> {
                        deleteStudent(event.uid)
                    }
                    UserType.TEACHER -> {
                        deleteTeacher(event.uid, false)
                    }
                }
            }
            is PeopleUiEvent.OnFilterChange -> {
                when (event.peopleFilterType) {
                    PeopleFilterType.ALL -> {
                        uiState = uiState.copy(
                            dataState = DataState.SUCCESS,
                            filter = event.peopleFilterType,
                            isFabExpanded = false,
                            peoples = peoples
                        )
                    }
                    PeopleFilterType.TEACHERS -> {
                        uiState = uiState.copy(
                            dataState = DataState.SUCCESS,
                            filter = event.peopleFilterType,
                            isFabExpanded = false,
                            peoples = teachers()
                        )
                    }
                    PeopleFilterType.STUDENTS -> {
                        val students = students()
                        uiState = uiState.copy(
                            dataState = if (students.isEmpty()) {
                                DataState.EMPTY(
                                    message = UiText.StringResource(
                                        Strings.invite_students_to_your_class
                                    )
                                )
                            } else {
                                DataState.SUCCESS
                            },
                            filter = event.peopleFilterType,
                            isFabExpanded = students.isEmpty(),
                            peoples = students
                        )
                    }
                }
            }
            is PeopleUiEvent.OnLeaveClass -> {
                deleteTeacher(event.uid, true)
            }
            is PeopleUiEvent.OnOpenFabMenuChange -> {
                uiState = uiState.copy(openFabMenu = event.open)
            }
            is PeopleUiEvent.OnOpenLeaveClassDialogChange -> {
                uiState = uiState.copy(openLeaveClassDialog = event.open)
            }
            is PeopleUiEvent.OnRefresh -> {
                fetchPeoples(true)
            }
            is PeopleUiEvent.OnRetry -> {
                fetchPeoples(false)
            }
            is PeopleUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun fetchPeoples(refreshing: Boolean) {
        // DataState.LOADING is only used when initial loading and retry.
        // Otherwise show the PullRefreshIndicator using refreshing = true
        // Likewise, DataState.ERROR is only used when initial loading and retry.
        // Otherwise show snackbar by using userMessage
        courseId?.let { id ->
            getPeoplesUseCase(id).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        uiState = if (refreshing) {
                            uiState.copy(refreshing = true)
                        } else {
                            uiState.copy(dataState = DataState.LOADING)
                        }
                    }
                    is Resource.Success -> {
                        peoples = resource.data ?: emptyList()
                        when (uiState.filter) {
                            PeopleFilterType.ALL -> {
                                uiState = uiState.copy(
                                    dataState = DataState.SUCCESS,
                                    isFabExpanded = false,
                                    peoples = peoples,
                                    refreshing = false
                                )
                            }
                            PeopleFilterType.TEACHERS -> {
                                uiState = uiState.copy(
                                    dataState = DataState.SUCCESS,
                                    isFabExpanded = false,
                                    peoples = teachers(),
                                    refreshing = false
                                )
                            }
                            PeopleFilterType.STUDENTS -> {
                                val students = students()
                                uiState = uiState.copy(
                                    dataState = if (students.isEmpty()) {
                                        DataState.EMPTY(
                                            message = UiText.StringResource(
                                                Strings.invite_students_to_your_class
                                            )
                                        )
                                    } else {
                                        DataState.SUCCESS
                                    },
                                    isFabExpanded = students.isEmpty(),
                                    peoples = students,
                                    refreshing = false
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        uiState = if (refreshing) {
                            uiState.copy(
                                refreshing = false,
                                userMessage = resource.message
                            )
                        } else {
                            uiState.copy(dataState = DataState.ERROR(message = resource.message!!))
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun deleteTeacher(uid: String, isLeaving: Boolean) {
        // if user is leaving exit page on success
        // else refresh peoples
        courseId?.let { id ->
            deleteTeacherUseCase(id, uid).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        val progressDialogTextId = if (isLeaving) {
                            Strings.leaving_class
                        } else {
                            Strings.removing_teacher
                        }

                        uiState = uiState.copy(
                            openProgressDialog = true,
                            progressDialogText = UiText.StringResource(progressDialogTextId)
                        )
                    }
                    is Resource.Success -> {
                        if (isLeaving) {
                            uiState = uiState.copy(
                                isUserLeaveClass = true,
                                openProgressDialog = false
                            )
                        } else {
                            uiState = uiState.copy(openProgressDialog = false)
                            fetchPeoples(true)
                        }
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
    }

    private fun deleteStudent(uid: String) {
        courseId?.let { id ->
            deleteStudentUseCase(id, uid).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        uiState = uiState.copy(
                            openProgressDialog = true,
                            progressDialogText = UiText.StringResource(Strings.removing_student)
                        )
                    }
                    is Resource.Success -> {
                        uiState = uiState.copy(openProgressDialog = false)
                        fetchPeoples(true)
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
    }

    private fun teachers(): List<User> {
        return peoples.filter {
            it.teaching?.contains(courseId) ?: false
        }
    }

    private fun students(): List<User> {
        return peoples.filter {
            it.enrolled?.contains(courseId) ?: false
        }
    }
}