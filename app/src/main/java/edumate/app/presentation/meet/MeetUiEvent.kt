package edumate.app.presentation.meet

import edumate.app.domain.model.courses.Course
import edumate.app.domain.model.meetings.Meeting

sealed class MeetUiEvent {
    data class DeleteMeeting(val id: String) : MeetUiEvent()
    data class EndMeeting(val meeting: Meeting) : MeetUiEvent()
    data class OnAppBarMenuExpandedChange(val expanded: Boolean) : MeetUiEvent()
    data class OnCreate(val course: Course) : MeetUiEvent()
    object CreateMeeting : MeetUiEvent()
    object OnRefresh : MeetUiEvent()
    object OnRetry : MeetUiEvent()
    object UserMessageShown : MeetUiEvent()
}