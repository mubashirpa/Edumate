package edumate.app.presentation.teaching

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.Result
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.classroom.courses.DeleteCourseUseCase
import edumate.app.domain.usecase.classroom.courses.ListCoursesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TeachingViewModel
    @Inject
    constructor(
        getCurrentUserUseCase: GetCurrentUserUseCase,
        private val listCoursesUseCase: ListCoursesUseCase,
        private val deleteCourseUseCase: DeleteCourseUseCase,
    ) : ViewModel() {
        var uiState by mutableStateOf(TeachingUiState())
            private set

        private var currentUser: FirebaseUser? = null
        private var listCoursesJob: Job? = null

        init {
            getCurrentUserUseCase().map { user ->
                currentUser = user
                fetchCourses(user?.uid, false)
            }.launchIn(viewModelScope)
        }

        fun onEvent(event: TeachingUiEvent) {
            when (event) {
                is TeachingUiEvent.OnDeleteCourse -> {
                    deleteCourse(event.courseId)
                }

                is TeachingUiEvent.OnOpenDeleteCourseDialogChange -> {
                    uiState = uiState.copy(deleteCourseId = event.courseId)
                }

                TeachingUiEvent.OnRefresh -> {
                    fetchCourses(currentUser?.uid, true)
                }

                TeachingUiEvent.UserMessageShown -> {
                    uiState = uiState.copy(userMessage = null)
                }
            }
        }

        private fun fetchCourses(
            teacherId: String?,
            refreshing: Boolean,
        ) {
            // Cancel ongoing listCoursesJob before recall.
            listCoursesJob?.cancel()
            listCoursesJob =
                listCoursesUseCase(teacherId = teacherId).onEach { result ->
                    if (refreshing) {
                        when (result) {
                            is Result.Empty -> {}

                            is Result.Loading -> {
                                uiState = uiState.copy(refreshing = true)
                            }

                            is Result.Success -> {
                                uiState =
                                    uiState.copy(
                                        teachingCoursesResult = result,
                                        refreshing = false,
                                    )
                            }

                            is Result.Error -> {
                                uiState =
                                    if (uiState.teachingCoursesResult is Result.Loading) {
                                        uiState.copy(
                                            teachingCoursesResult = result,
                                            refreshing = false,
                                        )
                                    } else {
                                        uiState.copy(
                                            refreshing = false,
                                            userMessage = result.message,
                                        )
                                    }
                            }
                        }
                    } else {
                        uiState = uiState.copy(teachingCoursesResult = result)
                    }
                }.launchIn(viewModelScope)
        }

        private fun deleteCourse(courseId: String) {
            // TODO("Delete other resources related to course like announcements")
            deleteCourseUseCase(courseId).onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Loading -> {
                        uiState =
                            uiState.copy(
                                deleteCourseId = null,
                                openProgressDialog = true,
                            )
                    }

                    is Result.Success -> {
                        uiState = uiState.copy(openProgressDialog = false)
                        fetchCourses(currentUser?.uid, true)
                    }

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = result.message,
                            )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
