package edumate.app.presentation.people

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.R.string as Strings
import edumate.app.core.DataState
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.core.utils.moveItemToFirstPosition
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.domain.usecase.ListPeoples
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.students.DeleteStudent
import edumate.app.domain.usecase.teachers.DeleteTeacher
import edumate.app.navigation.Routes
import edumate.app.presentation.class_details.UserType
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class PeopleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val listPeoplesUseCase: ListPeoples,
    private val deleteTeacherUseCase: DeleteTeacher,
    private val deleteStudentUseCase: DeleteStudent
) : ViewModel() {

    var uiState by mutableStateOf(PeopleUiState())
        private set

    private val courseId: String = checkNotNull(savedStateHandle[Routes.Args.PEOPLE_COURSE_ID])
    private val ownerId: String = checkNotNull(savedStateHandle[Routes.Args.PEOPLE_COURSE_OWNER_ID])
    private var peoples: List<UserProfile> = emptyList()
    private var listPeoplesJob: Job? = null

    init {
        getCurrentUserUseCase().map { user ->
            uiState = uiState.copy(currentUser = user)
        }.launchIn(viewModelScope)
        fetchPeoples(false)
    }

    fun onEvent(event: PeopleUiEvent) {
        when (event) {
            is PeopleUiEvent.OnAppBarMenuExpandedChange -> {
                uiState = uiState.copy(appBarMenuExpanded = event.expanded)
            }

            is PeopleUiEvent.OnFilterChange -> {
                when (event.peopleFilterType) {
                    PeopleFilterType.ALL -> {
                        uiState = uiState.copy(
                            dataState = DataState.SUCCESS,
                            filter = event.peopleFilterType,
                            peoples = peoples
                        )
                    }

                    PeopleFilterType.TEACHERS -> {
                        uiState = uiState.copy(
                            dataState = DataState.SUCCESS,
                            filter = event.peopleFilterType,
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

            is PeopleUiEvent.OnOpenRemoveUserDialogChange -> {
                uiState = uiState.copy(removeUserProfile = event.userProfile)
            }

            is PeopleUiEvent.OnRemoveUser -> {
                when (event.userType) {
                    UserType.STUDENT -> {
                        deleteStudent(event.uid)
                    }

                    UserType.TEACHER -> {
                        deleteTeacher(event.uid, false)
                    }
                }
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
        // DataState.LOADING is only used when initial loading and retry
        // Otherwise show the PullRefreshIndicator using refreshing = true
        // Likewise, DataState.ERROR is only used when initial loading and retry
        // Otherwise show snackbar by using userMessage.
        // Cancel ongoing listPeoplesJob before recall.
        listPeoplesJob?.cancel()
        listPeoplesJob = listPeoplesUseCase(courseId).onEach { resource ->
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
                    peoples = peoples.moveItemToFirstPosition { it.id == ownerId }

                    if (peoples.isNotEmpty()) {
                        when (uiState.filter) {
                            PeopleFilterType.ALL -> {
                                uiState = uiState.copy(
                                    dataState = DataState.SUCCESS,
                                    peoples = peoples,
                                    refreshing = false
                                )
                            }

                            PeopleFilterType.TEACHERS -> {
                                uiState = uiState.copy(
                                    dataState = DataState.SUCCESS,
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
                                    peoples = students,
                                    refreshing = false
                                )
                            }
                        }
                    } else {
                        uiState = uiState.copy(
                            dataState = DataState.ERROR(
                                UiText.StringResource(Strings.error_unexpected)
                            )
                        )
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

    private fun deleteTeacher(userId: String, isLeaving: Boolean) {
        // if userProfile is leaving exit page on success
        // else refresh peoples
        deleteTeacherUseCase(courseId, userId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(
                        openProgressDialog = true,
                        removeUserProfile = null
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
                        userMessage = if (isLeaving) {
                            UiText.StringResource(Strings.unable_to_leave_class)
                        } else {
                            UiText.StringResource(Strings.unable_to_remove_teacher)
                        }
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteStudent(userId: String) {
        deleteStudentUseCase(courseId, userId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(
                        openProgressDialog = true,
                        removeUserProfile = null
                    )
                }

                is Resource.Success -> {
                    uiState = uiState.copy(openProgressDialog = false)
                    fetchPeoples(true)
                }

                is Resource.Error -> {
                    uiState = uiState.copy(
                        openProgressDialog = false,
                        userMessage = UiText.StringResource(Strings.unable_to_remove_student)
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun teachers(): List<UserProfile> {
        return peoples.filter {
            it.teaching.contains(courseId)
        }
    }

    private fun students(): List<UserProfile> {
        return peoples.filter {
            it.enrolled.contains(courseId)
        }
    }
}