package edumate.app.domain.repository

import edumate.app.data.remote.dto.MeetingDto
import edumate.app.domain.model.meetings.MeetingState

interface MeetingsRepository {

    suspend fun create(courseId: String, meetingDto: MeetingDto): MeetingDto?

    suspend fun delete(courseId: String, id: String)

    suspend fun get(courseId: String, id: String): MeetingDto?

    suspend fun list(
        courseId: String,
        meetingStates: List<MeetingState> = listOf(MeetingState.LIVE),
        orderBy: String = "updateTime desc",
        pageSize: Int? = null
    ): List<MeetingDto>

    suspend fun patch(
        courseId: String,
        id: String,
        meetingDto: MeetingDto
    ): MeetingDto?
}