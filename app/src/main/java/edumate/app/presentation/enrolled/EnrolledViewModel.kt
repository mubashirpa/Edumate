package edumate.app.presentation.enrolled

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.R.string as Strings
import edumate.app.core.DataState
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.courses.ListCourses
import edumate.app.domain.usecase.students.DeleteStudent
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class EnrolledViewModel @Inject constructor(
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val listCoursesUseCase: ListCourses,
    private val deleteStudentUseCase: DeleteStudent
) : ViewModel() {

    var uiState by mutableStateOf(EnrolledUiState())
        private set

    private var currentUser: FirebaseUser? = null
    private var listCoursesJob: Job? = null

    init {
        getCurrentUserUseCase().map { user ->
            currentUser = user
            fetchClasses(user?.uid, false)
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: EnrolledUiEvent) {
        when (event) {
            is EnrolledUiEvent.Unenroll -> {
                if (currentUser != null) {
                    unEnroll(event.courseId, currentUser!!.uid)
                }
            }

            EnrolledUiEvent.OnRefresh -> {
                fetchClasses(currentUser?.uid, true)
            }

            EnrolledUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun fetchClasses(studentId: String?, refreshing: Boolean) {
        // Cancel ongoing listCoursesJob before recall.
        listCoursesJob?.cancel()
        listCoursesJob = listCoursesUseCase(studentId = studentId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = if (refreshing) {
                        uiState.copy(refreshing = true)
                    } else {
                        uiState.copy(dataState = DataState.LOADING)
                    }
                }

                is Resource.Success -> {
                    val courses = resource.data
                    uiState = if (courses.isNullOrEmpty()) {
                        uiState.copy(
                            dataState = DataState.EMPTY(
                                UiText.StringResource(Strings.join_a_class_to_get_started)
                            ),
                            refreshing = false
                        )
                    } else {
                        uiState.copy(
                            courses = courses,
                            dataState = DataState.SUCCESS,
                            refreshing = false
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

    private fun unEnroll(courseId: String, userId: String) {
        deleteStudentUseCase(courseId, userId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    uiState = uiState.copy(openProgressDialog = false)
                    fetchClasses(currentUser?.uid, true)
                }

                is Resource.Error -> {
                    uiState = uiState.copy(
                        openProgressDialog = false,
                        userMessage = UiText.StringResource(Strings.unable_to_leave_class)
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}