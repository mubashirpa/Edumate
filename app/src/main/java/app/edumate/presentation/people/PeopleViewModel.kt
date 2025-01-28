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
import app.edumate.domain.model.users.UserRole
import app.edumate.domain.model.users.Users
import app.edumate.domain.usecase.authentication.GetCurrentUserUseCase
import app.edumate.domain.usecase.member.DeleteMemberUseCase
import app.edumate.domain.usecase.member.GetMembersUseCase
import app.edumate.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PeopleViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMembersUseCase: GetMembersUseCase,
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
            is PeopleUiEvent.OnDeletePerson -> {
                deletePerson(courseId = args.courseId, userId = event.userId, isUserLeaving = false)
            }

            is PeopleUiEvent.OnExpandedAppBarDropdownChange -> {
                uiState = uiState.copy(expandedAppBarDropdown = event.expanded)
            }

            is PeopleUiEvent.OnFilterChange -> {
                uiState = uiState.copy(filter = event.type)
            }

            is PeopleUiEvent.OnLeaveClass -> {
                deletePerson(courseId = args.courseId, userId = event.userId, isUserLeaving = true)
            }

            is PeopleUiEvent.OnOpenDeleteUserDialogChange -> {
                uiState = uiState.copy(deletePerson = event.user)
            }

            is PeopleUiEvent.OnOpenLeaveClassDialogChange -> {
                uiState = uiState.copy(openLeaveClassDialog = event.open)
            }

            PeopleUiEvent.OnRefresh -> {
                getPeoples(args.courseId, true)
            }

            PeopleUiEvent.OnRetry -> {
                getPeoples(args.courseId, false)
            }

            is PeopleUiEvent.OnShowInviteBottomSheetChange -> {
                uiState = uiState.copy(showInviteBottomSheet = event.show)
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
        // Cancel any ongoing listStudentsJob before making a new call.
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
                            var isCurrentUserTeacher = false
                            val students = mutableListOf<Users>()
                            val teachers = mutableListOf<Users>()

                            peoples.forEach { person ->
                                if (person.user?.id == uiState.currentUserId) {
                                    isCurrentUserTeacher = person.role == UserRole.TEACHER
                                }
                                when (person.role!!) {
                                    UserRole.STUDENT -> students.add(person)
                                    UserRole.TEACHER -> teachers.add(person)
                                }
                            }

                            uiState =
                                uiState.copy(
                                    isCurrentUserTeacher = isCurrentUserTeacher,
                                    isRefreshing = false,
                                    peopleResult = result,
                                    students = students,
                                    teachers = teachers,
                                )
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
