package app.edumate.presentation.people

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
import app.edumate.domain.model.member.UserRole
import app.edumate.domain.model.user.User
import app.edumate.domain.usecase.authentication.GetCurrentUserUseCase
import app.edumate.domain.usecase.member.DeleteMemberUseCase
import app.edumate.domain.usecase.member.GetMembersUseCase
import app.edumate.domain.usecase.member.UpdateMemberUseCase
import app.edumate.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PeopleViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMembersUseCase: GetMembersUseCase,
    private val updateMemberUseCase: UpdateMemberUseCase,
    private val deleteMemberUseCase: DeleteMemberUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(PeopleUiState())
        private set

    private val args = savedStateHandle.toRoute<Screen.People>()
    private var getPeoplesJob: Job? = null

    init {
        getCurrentUser()
        getPeoples(args.courseId, false)
    }

    fun onEvent(event: PeopleUiEvent) {
        when (event) {
            is PeopleUiEvent.ChangePersonRole -> {
                updatePerson(
                    courseId = args.courseId,
                    userId = event.userId,
                    role = event.role,
                )
            }

            is PeopleUiEvent.DeletePerson -> {
                deletePerson(courseId = args.courseId, userId = event.userId, isUserLeaving = false)
            }

            is PeopleUiEvent.LeaveClass -> {
                deletePerson(courseId = args.courseId, userId = event.userId, isUserLeaving = true)
            }

            is PeopleUiEvent.OnExpandedAppBarDropdownChange -> {
                uiState = uiState.copy(expandedAppBarDropdown = event.expanded)
            }

            is PeopleUiEvent.OnFilterValueChange -> {
                uiState = uiState.copy(filter = event.type)
            }

            is PeopleUiEvent.OnOpenDeleteUserDialogChange -> {
                uiState = uiState.copy(deletePerson = event.user)
            }

            is PeopleUiEvent.OnOpenLeaveClassDialogChange -> {
                uiState = uiState.copy(openLeaveClassDialog = event.open)
            }

            is PeopleUiEvent.OnShowInviteBottomSheetChange -> {
                uiState = uiState.copy(showInviteBottomSheet = event.show)
            }

            PeopleUiEvent.Refresh -> {
                getPeoples(
                    courseId = args.courseId,
                    isRefreshing = uiState.peopleResult is Result.Success,
                )
            }

            PeopleUiEvent.Retry -> {
                getPeoples(courseId = args.courseId, isRefreshing = false)
            }

            PeopleUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun getCurrentUser() {
        getCurrentUserUseCase()
            .onEach { result ->
                if (result is Result.Success) {
                    uiState = uiState.copy(currentUserId = result.data?.id)
                }
            }.launchIn(viewModelScope)
    }

    private fun getPeoples(
        courseId: String,
        isRefreshing: Boolean,
    ) {
        // Cancel any ongoing getPeoplesJob before making a new call.
        getPeoplesJob?.cancel()
        getPeoplesJob =
            getMembersUseCase(courseId)
                .onEach { result ->
                    when (result) {
                        is Result.Empty -> {}

                        is Result.Error -> {
                            // The Result.Error state is only used during initial loading and retry attempts.
                            // Otherwise, a snackbar is displayed using the userMessage property.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(
                                        isRefreshing = false,
                                        userMessage = result.message,
                                    )
                                } else {
                                    uiState.copy(peopleResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with isRefreshing = true.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(isRefreshing = true)
                                } else {
                                    uiState.copy(peopleResult = result)
                                }
                        }

                        is Result.Success -> {
                            val peoples = result.data.orEmpty()
                            val students = mutableListOf<User>()
                            val teachers = mutableListOf<User>()

                            peoples.forEach { person ->
                                when (person.role!!) {
                                    UserRole.STUDENT -> students.add(person)
                                    UserRole.TEACHER -> teachers.add(person)
                                }
                            }

                            uiState =
                                uiState.copy(
                                    isRefreshing = false,
                                    peopleResult = result,
                                    students = students,
                                    teachers = teachers,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
    }

    private fun updatePerson(
        courseId: String,
        userId: String,
        role: UserRole,
    ) {
        updateMemberUseCase(courseId, userId, role)
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
                        uiState = uiState.copy(openProgressDialog = false)
                        getPeoples(args.courseId, true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun deletePerson(
        courseId: String,
        userId: String,
        isUserLeaving: Boolean,
    ) {
        val isTeacher = uiState.deletePerson?.role == UserRole.TEACHER

        deleteMemberUseCase(courseId, userId)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage =
                                    when {
                                        isUserLeaving -> UiText.StringResource(R.string.unable_to_leave_class)
                                        isTeacher -> UiText.StringResource(R.string.unable_to_remove_teacher)
                                        else -> UiText.StringResource(R.string.unable_to_remove_student)
                                    },
                            )
                    }

                    is Result.Loading -> {
                        uiState =
                            uiState.copy(
                                deletePerson = null,
                                openProgressDialog = true,
                            )
                    }

                    is Result.Success -> {
                        if (isUserLeaving) {
                            uiState =
                                uiState.copy(
                                    isUserLeftCourse = true,
                                    openProgressDialog = false,
                                )
                        } else {
                            uiState = uiState.copy(openProgressDialog = false)
                            getPeoples(args.courseId, true)
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }
}
