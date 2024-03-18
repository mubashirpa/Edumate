package edumate.app.presentation.classwork

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Result
import edumate.app.domain.usecase.authentication.GetCurrentUserIdUseCase
import edumate.app.domain.usecase.classroom.courseWork.DeleteCourseWorkUseCase
import edumate.app.domain.usecase.classroom.courseWork.ListCourseWorksUseCase
import edumate.app.navigation.Routes
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ClassworkViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
        private val deleteCourseWorkUseCase: DeleteCourseWorkUseCase,
        private val listCourseWorksUseCase: ListCourseWorksUseCase,
    ) : ViewModel() {
        var uiState by mutableStateOf(ClassworkUiState())
            private set

        private val courseId: String = checkNotNull(savedStateHandle[Routes.Args.CLASSWORK_COURSE_ID])
        private var listCourseWorksJob: Job? = null

        init {
            uiState = uiState.copy(userId = getCurrentUserIdUseCase())
            listCourseWork(false)
        }

        fun onEvent(event: ClassworkUiEvent) {
            when (event) {
                is ClassworkUiEvent.OnAppBarDropdownExpandedChange -> {
                    uiState = uiState.copy(appBarDropdownExpanded = event.expanded)
                }

                is ClassworkUiEvent.OnDeleteCourseWork -> {
                    deleteCourseWork(event.id)
                }

                is ClassworkUiEvent.OnOpenDeleteCourseWorkDialogChange -> {
                    uiState = uiState.copy(deleteCourseWork = event.courseWork)
                }

                is ClassworkUiEvent.OnShowCreateCourseWorkBottomSheetChange -> {
                    uiState = uiState.copy(showCreateCourseWorkBottomSheet = event.show)
                }

                ClassworkUiEvent.OnRefresh -> {
                    listCourseWork(true)
                }

                ClassworkUiEvent.OnRetry -> {
                    listCourseWork(false)
                }

                ClassworkUiEvent.UserMessageShown -> {
                    uiState = uiState.copy(userMessage = null)
                }
            }
        }

        private fun listCourseWork(isRefreshing: Boolean) {
            // Cancel any ongoing listCourseWorksJob before making a new call.
            listCourseWorksJob?.cancel()
            listCourseWorksJob =
                listCourseWorksUseCase(
                    courseId = courseId,
                    orderBy = "creationTime desc",
                ).onEach { result ->
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
                                    uiState.copy(courseWorkResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with isRefreshing = true.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(isRefreshing = true)
                                } else {
                                    uiState.copy(courseWorkResult = result)
                                }
                        }

                        is Result.Success -> {
                            uiState =
                                uiState.copy(
                                    courseWorkResult = result,
                                    isRefreshing = false,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
        }

        private fun deleteCourseWork(id: String) {
            deleteCourseWorkUseCase(courseId, id).onEach { result ->
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
                        uiState =
                            uiState.copy(
                                deleteCourseWork = null,
                                openProgressDialog = true,
                            )
                    }

                    is Result.Success -> {
                        uiState = uiState.copy(openProgressDialog = false)
                        listCourseWork(true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
