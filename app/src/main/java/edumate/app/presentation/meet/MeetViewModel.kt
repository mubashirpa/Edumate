package edumate.app.presentation.meet

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.R
import edumate.app.core.DataState
import edumate.app.core.FirebaseConstants
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.model.AssigneeMode
import edumate.app.domain.model.meetings.Meeting
import edumate.app.domain.model.meetings.MeetingState
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.meetings.CreateMeeting
import edumate.app.domain.usecase.meetings.DeleteMeeting
import edumate.app.domain.usecase.meetings.ListMeetings
import edumate.app.domain.usecase.meetings.PatchMeeting
import edumate.app.navigation.Routes
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class MeetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val listMeetingsUseCase: ListMeetings,
    private val createMeetingUseCase: CreateMeeting,
    private val deleteMeetingUseCase: DeleteMeeting,
    private val patchMeetingUseCase: PatchMeeting
) : ViewModel() {

    var uiState by mutableStateOf(MeetUiState())
        private set

    private val courseId: String = checkNotNull(savedStateHandle[Routes.Args.MEET_SCREEN_COURSE_ID])
    private var listMeetingsJob: Job? = null
    private val meeting = mutableStateOf(Meeting())

    fun onEvent(event: MeetUiEvent) {
        when (event) {
            is MeetUiEvent.DeleteMeeting -> {
                deleteMeeting(event.id)
            }

            is MeetUiEvent.EndMeeting -> {
                updateMeeting(event.meeting.copy(state = MeetingState.ENDED))
            }

            is MeetUiEvent.OnAppBarMenuExpandedChange -> {
                uiState = uiState.copy(appBarMenuExpanded = event.expanded)
            }

            is MeetUiEvent.OnCreate -> {
                if (!uiState.onCreate) {
                    uiState = uiState.copy(onCreate = true)
                    getCurrentUserUseCase().map { user ->
                        if (user != null) {
                            uiState = uiState.copy(
                                currentUser = user,
                                isCurrentUserTeacher = event.course.teacherGroupId.contains(
                                    user.uid
                                )
                            )
                            initMeeting(user)
                            fetchMeetings(false)
                        }
                    }.launchIn(viewModelScope)
                }
            }

            MeetUiEvent.CreateMeeting -> {
                createMeeting()
            }

            MeetUiEvent.OnRefresh -> {
                fetchMeetings(true)
            }

            MeetUiEvent.OnRetry -> {
                fetchMeetings(refreshing = false)
            }

            MeetUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun fetchMeetings(refreshing: Boolean) {
        val meetingStates = if (uiState.isCurrentUserTeacher) {
            listOf(MeetingState.CREATED, MeetingState.LIVE, MeetingState.ENDED)
        } else {
            listOf(MeetingState.LIVE)
        }
        listMeetingsJob?.cancel()
        listMeetingsJob = listMeetingsUseCase(
            courseId,
            meetingStates
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = if (refreshing) {
                        uiState.copy(refreshing = true)
                    } else {
                        uiState.copy(dataState = DataState.LOADING)
                    }
                }

                is Resource.Success -> {
                    val meetings = resource.data
                    uiState = if (meetings.isNullOrEmpty()) {
                        uiState.copy(
                            // Message is set from ui
                            dataState = DataState.EMPTY(UiText.Empty),
                            refreshing = false
                        )
                    } else {
                        uiState.copy(
                            dataState = DataState.SUCCESS,
                            meetings = meetings,
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

    private fun createMeeting() {
        meeting.value = meeting.value.copy(
            title = null
        )

        createMeetingUseCase(courseId, meeting.value).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    val meetingResponse = resource.data
                    if (uiState.currentUser != null) {
                        initMeeting(uiState.currentUser!!)
                    }
                    if (meetingResponse != null) {
                        uiState = uiState.copy(openProgressDialog = false)
                        fetchMeetings(true)
                    } else {
                        uiState = uiState.copy(
                            openProgressDialog = false,
                            userMessage = UiText.StringResource(R.string.error_unexpected)
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

    private fun deleteMeeting(id: String) {
        deleteMeetingUseCase(courseId, id).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    uiState = uiState.copy(openProgressDialog = false)
                    fetchMeetings(true)
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

    private fun updateMeeting(meeting: Meeting) {
        patchMeetingUseCase(courseId, meeting.id, meeting).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    val meetingResponse = resource.data
                    if (meetingResponse != null) {
                        uiState = uiState.copy(openProgressDialog = false)
                        fetchMeetings(true)
                    } else {
                        uiState = uiState.copy(
                            openProgressDialog = false,
                            userMessage = UiText.StringResource(R.string.error_unexpected)
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

    private fun initMeeting(user: FirebaseUser) {
        val id = generateMeetingId()
        val idPrefix = id.replace("-", "").take(3)
        val idSuffix = id.replace("-", "").takeLast(3)
        val meetingId = ("$idPrefix-${user.uid.take(4)}-$idSuffix").lowercase()

        meeting.value = meeting.value.copy(
            alternateLink = "https://edumateapp.web.app/c/p/meet?cid=KI0I8TRVyCqEjyXGrMhp&mid=$meetingId",
            assigneeMode = AssigneeMode.ALL_STUDENTS,
            courseId = courseId,
            creatorUserId = user.uid,
            id = id,
            meetingId = meetingId,
            state = MeetingState.CREATED
        )
    }

    private fun generateMeetingId(): String {
        return FirebaseDatabase.getInstance()
            .getReference(FirebaseConstants.Database.MEETINGS_PATH)
            .child(courseId).push().key.orEmpty()
    }
}