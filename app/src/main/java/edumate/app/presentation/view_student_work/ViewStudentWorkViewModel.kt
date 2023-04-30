package edumate.app.presentation.view_student_work

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
import edumate.app.domain.model.student_submissions.SubmissionState
import edumate.app.domain.usecase.student_submissions.GetStudentSubmission
import edumate.app.domain.usecase.student_submissions.PatchStudentSubmission
import edumate.app.domain.usecase.student_submissions.ReturnStudentSubmission
import edumate.app.navigation.Routes
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class ViewStudentWorkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getStudentSubmission: GetStudentSubmission,
    private val patchStudentSubmission: PatchStudentSubmission,
    private val returnStudentSubmission: ReturnStudentSubmission
) : ViewModel() {

    var uiState by mutableStateOf(ViewStudentWorkUiState())
        private set

    private val courseId: String =
        checkNotNull(savedStateHandle[Routes.Args.VIEW_STUDENT_WORK_COURSE_ID])
    private val courseWorkId: String =
        checkNotNull(savedStateHandle[Routes.Args.VIEW_STUDENT_WORK_COURSE_WORK_ID])
    private val studentWorkId: String =
        checkNotNull(savedStateHandle[Routes.Args.VIEW_STUDENT_WORK_ID])
    private var getStudentSubmissionJob: Job? = null

    init {
        fetchStudentWork(false)
    }

    fun onEvent(event: ViewStudentWorkUiEvent) {
        when (event) {
            is ViewStudentWorkUiEvent.OnAppBarMenuExpandedChange -> {
                uiState = uiState.copy(appBarMenuExpanded = event.expanded)
            }

            is ViewStudentWorkUiEvent.OnGradeChange -> {
                uiState = uiState.copy(grade = event.grade)
            }

            is ViewStudentWorkUiEvent.OnOpenReturnDialog -> {
                uiState = uiState.copy(openReturnDialog = event.open)
            }

            ViewStudentWorkUiEvent.OnRefresh -> {
                fetchStudentWork(true)
            }

            ViewStudentWorkUiEvent.PatchStudentWork -> {
                patchStudentWork()
            }

            ViewStudentWorkUiEvent.ReturnStudentWork -> {
                returnStudentWork()
            }

            ViewStudentWorkUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun fetchStudentWork(refreshing: Boolean) {
        // Cancel ongoing getStudentSubmissionJob before recall.
        getStudentSubmissionJob?.cancel()
        getStudentSubmissionJob =
            getStudentSubmission(courseId, courseWorkId, studentWorkId).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        uiState = if (refreshing) {
                            uiState.copy(refreshing = true)
                        } else {
                            uiState.copy(dataState = DataState.LOADING)
                        }
                    }

                    is Resource.Success -> {
                        val studentWork = resource.data
                        uiState = if (studentWork != null) {
                            uiState.copy(
                                dataState = DataState.SUCCESS,
                                grade = studentWork.assignedGrade?.toString().orEmpty(),
                                refreshing = false,
                                studentWork = studentWork
                            )
                        } else {
                            uiState.copy(
                                dataState = DataState.EMPTY(
                                    UiText.StringResource(Strings.student_not_yet_submitted)
                                ),
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

    private fun patchStudentWork() {
        if (uiState.studentWork != null) {
            val assignedGrade = try {
                uiState.grade.toInt()
            } catch (e: NumberFormatException) {
                null
            }
            val studentWork = mutableStateOf(uiState.studentWork!!)

            studentWork.value = studentWork.value.copy(assignedGrade = assignedGrade)

            patchStudentSubmission(
                courseId,
                courseWorkId,
                studentWorkId,
                studentWork.value
            ).onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }

                    is Resource.Success -> {
                        val updatedStudentWork = resource.data
                        if (updatedStudentWork != null) {
                            val isTurnedIn = uiState.studentWork?.state == SubmissionState.TURNED_IN
                            uiState = uiState.copy(
                                openProgressDialog = isTurnedIn,
                                studentWork = updatedStudentWork
                            )
                            if (isTurnedIn) {
                                returnStudentWork()
                            }
                        } else {
                            uiState = uiState.copy(
                                dataState = DataState.EMPTY(
                                    UiText.StringResource(Strings.error_unexpected)
                                ),
                                openProgressDialog = false
                            )
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

    private fun returnStudentWork() {
        returnStudentSubmission(courseId, courseWorkId, studentWorkId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    if (uiState.studentWork != null) {
                        val courseWork = mutableStateOf(uiState.studentWork!!)
                        courseWork.value = courseWork.value.copy(state = SubmissionState.RETURNED)

                        uiState = uiState.copy(
                            openProgressDialog = false,
                            studentWork = courseWork.value
                        )
                    } else {
                        uiState = uiState.copy(
                            openProgressDialog = false,
                            userMessage = UiText.StringResource(Strings.error_unexpected)
                        )
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