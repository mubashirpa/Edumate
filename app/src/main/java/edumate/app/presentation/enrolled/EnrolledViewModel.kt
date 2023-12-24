package edumate.app.presentation.enrolled

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.UiText
import edumate.app.core.utils.ResourceNew
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.courses.ListCourses
import edumate.app.domain.usecase.students.DeleteStudent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import edumate.app.R.string as Strings

@HiltViewModel
class EnrolledViewModel
    @Inject
    constructor(
        getCurrentUserUseCase: GetCurrentUserUseCase,
        private val listCoursesUseCase: ListCourses,
        private val deleteStudentUseCase: DeleteStudent,
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
                is EnrolledUiEvent.OnOpenUnEnrolDialogChange -> {
                    uiState = uiState.copy(unEnrolCourseId = event.courseId)
                }

                is EnrolledUiEvent.OnUnEnroll -> {
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

        private fun fetchClasses(
            studentId: String?,
            refreshing: Boolean,
        ) {
            // Cancel ongoing listCoursesJob before recall.
            listCoursesJob?.cancel()
            listCoursesJob =
                listCoursesUseCase(studentId = studentId).onEach { resource ->
                    if (refreshing) {
                        when (resource) {
                            is ResourceNew.Unknown -> {}

                            is ResourceNew.Loading -> {
                                uiState = uiState.copy(refreshing = true)
                            }

                            is ResourceNew.Success -> {
                                uiState =
                                    uiState.copy(
                                        enrolledCoursesResource = resource,
                                        refreshing = false,
                                    )
                            }

                            is ResourceNew.Error -> {
                                uiState =
                                    if (uiState.enrolledCoursesResource is ResourceNew.Loading) {
                                        uiState.copy(enrolledCoursesResource = resource, refreshing = false)
                                    } else {
                                        uiState.copy(
                                            refreshing = false,
                                            userMessage = resource.message,
                                        )
                                    }
                            }
                        }
                    } else {
                        uiState = uiState.copy(enrolledCoursesResource = resource)
                    }
                }.launchIn(viewModelScope)
        }

        private fun unEnroll(
            courseId: String,
            userId: String,
        ) {
            // TODO("Delete other resources related to course")
            deleteStudentUseCase(courseId, userId).onEach { resource ->
                when (resource) {
                    is ResourceNew.Unknown -> {}

                    is ResourceNew.Loading -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = true,
                                unEnrolCourseId = null,
                            )
                    }

                    is ResourceNew.Success -> {
                        uiState = uiState.copy(openProgressDialog = false)
                        fetchClasses(currentUser?.uid, true)
                    }

                    is ResourceNew.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = UiText.StringResource(Strings.unable_to_leave_class),
                            )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
