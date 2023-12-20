package edumate.app.data.remote.mapper

import edumate.app.data.remote.dto.MeetingDto
import edumate.app.domain.model.meetings.Meeting

fun MeetingDto.toMeeting(): Meeting {
    return Meeting(
        alternateLink,
        assigneeMode,
        courseId,
        creationTime,
        creatorUserId,
        id,
        individualStudentsOptions,
        meetingId,
        state,
        title,
        updateTime,
    )
}

fun Meeting.toMeetingDto(): MeetingDto {
    return MeetingDto(
        alternateLink,
        assigneeMode,
        courseId,
        creationTime,
        creatorUserId,
        id,
        individualStudentsOptions,
        meetingId,
        state,
        title,
        updateTime,
    )
}
