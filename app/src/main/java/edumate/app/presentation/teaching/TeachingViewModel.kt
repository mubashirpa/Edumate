package edumate.app.presentation.teaching

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.DataState
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.courses.DeleteCourse
import edumate.app.domain.usecase.courses.ListCourses
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import edumate.app.R.string as Strings

@HiltViewModel
class TeachingViewModel
    @Inject
    constructor(
        getCurrentUserUseCase: GetCurrentUserUseCase,
        private val listCoursesUseCase: ListCourses,
        private val deleteCourseUseCase: DeleteCourse,
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
                listCoursesUseCase(teacherId = teacherId).onEach { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            uiState =
                                if (refreshing) {
                                    uiState.copy(refreshing = true)
                                } else {
                                    uiState.copy(dataState = DataState.LOADING)
                                }
                        }

                        is Resource.Success -> {
                            val courses = resource.data
                            uiState =
                                if (courses.isNullOrEmpty()) {
                                    uiState.copy(
                                        dataState =
                                            DataState.EMPTY(
                                                UiText.StringResource(Strings.add_a_class_to_get_started),
                                            ),
                                        refreshing = false,
                                    )
                                } else {
                                    uiState.copy(
                                        courses = courses,
                                        dataState = DataState.SUCCESS,
                                        refreshing = false,
                                    )
                                }
                        }

                        is Resource.Error -> {
                            uiState =
                                if (refreshing) {
                                    uiState.copy(
                                        refreshing = false,
                                        userMessage = resource.message,
                                    )
                                } else {
                                    uiState.copy(dataState = DataState.ERROR(message = resource.message!!))
                                }
                        }
                    }
                }.launchIn(viewModelScope)
        }

        private fun deleteCourse(courseId: String) {
            // TODO("Delete other resources related to course like announcements")
            deleteCourseUseCase(courseId).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        uiState =
                            uiState.copy(
                                deleteCourseId = null,
                                openProgressDialog = true,
                            )
                    }

                    is Resource.Success -> {
                        uiState = uiState.copy(openProgressDialog = false)
                        fetchCourses(currentUser?.uid, true)
                    }

                    is Resource.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = resource.message,
                            )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
