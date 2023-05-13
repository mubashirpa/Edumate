package edumate.app.domain.usecase.meetings

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toMeeting
import edumate.app.data.remote.mapper.toMeetingDto
import edumate.app.domain.model.meetings.Meeting
import edumate.app.domain.repository.MeetingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CreateMeeting @Inject constructor(
    private val meetingsRepository: MeetingsRepository
) {
    operator fun invoke(courseId: String, meeting: Meeting): Flow<Resource<Meeting?>> = flow {
        try {
            emit(Resource.Loading())
            val meetingResponse =
                meetingsRepository.create(courseId, meeting.toMeetingDto())?.toMeeting()
            emit(Resource.Success(meetingResponse))
        } catch (e: Exception) {
            emit(
                Resource.Error(UiText.StringResource(Strings.unable_to_create_meeting))
            )
        }
    }
}