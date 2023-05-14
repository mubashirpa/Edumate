package edumate.app.presentation.meet

import edumate.app.domain.model.courses.Course
import edumate.app.domain.model.meetings.Meeting

sealed class MeetUiEvent {
    data class DeleteMeeting(val id: String) : MeetUiEvent()
    data class EndMeeting(val meeting: Meeting) : MeetUiEvent()
    data class OnAppBarMenuExpandedChange(val expanded: Boolean) : MeetUiEvent()
    data class OnCreate(val course: Course) : MeetUiEvent()
    data class StartMeeting(val meeting: Meeting) : MeetUiEvent()
    object CreateMeeting : MeetUiEvent()
    object OnLaunchMeetingSuccess : MeetUiEvent()
    object OnRefresh : MeetUiEvent()
    object OnRetry : MeetUiEvent()
    object OnUserMessageShown : MeetUiEvent()
}