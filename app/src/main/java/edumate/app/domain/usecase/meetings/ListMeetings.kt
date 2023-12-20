package edumate.app.domain.usecase.meetings

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toMeeting
import edumate.app.domain.model.meetings.Meeting
import edumate.app.domain.model.meetings.MeetingState
import edumate.app.domain.repository.MeetingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class ListMeetings
    @Inject
    constructor(
        private val meetingsRepository: MeetingsRepository,
    ) {
        operator fun invoke(
            courseId: String,
            meetingStates: List<MeetingState> = listOf(MeetingState.LIVE),
        ): Flow<Resource<List<Meeting>>> =
            flow {
                try {
                    emit(Resource.Loading())
                    val meetings = meetingsRepository.list(courseId, meetingStates).map { it.toMeeting() }
                    emit(Resource.Success(meetings.reversed()))
                } catch (e: Exception) {
                    emit(
                        Resource.Error(
                            UiText.StringResource(
                                Strings.cannot_retrieve_meetings_at_this_time_please_try_again_later,
                            ),
                        ),
                    )
                }
            }
    }
