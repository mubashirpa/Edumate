package edumate.app.presentation.meet

import edumate.app.domain.model.courses.Course
import edumate.app.domain.model.meetings.Meeting

sealed class MeetUiEvent {
    data class DeleteMeeting(val id: String) : MeetUiEvent()
    data class EndMeeting(val meeting: Meeting) : MeetUiEvent()
    data class OnAppBarMenuExpandedChange(val expanded: Boolean) : MeetUiEvent()
    data class OnCreate(val course: Course) : MeetUiEvent()
    data class StartMeeting(val meeting: Meeting) : MeetUiEvent()
    data object CreateMeeting : MeetUiEvent()
    data object OnLaunchMeetingSuccess : MeetUiEvent()
    data object OnRefresh : MeetUiEvent()
    data object OnRetry : MeetUiEvent()
    data object OnUserMessageShown : MeetUiEvent()
}