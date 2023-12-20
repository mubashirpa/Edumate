package edumate.app.data.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.getValue
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.MeetingDto
import edumate.app.domain.model.meetings.MeetingState
import edumate.app.domain.repository.MeetingsRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class MeetingsRepositoryImpl @Inject constructor(
    private val database: DatabaseReference
) : MeetingsRepository {

    override suspend fun create(courseId: String, meetingDto: MeetingDto): MeetingDto? {
        val id = meetingDto.id
        database.child(FirebaseConstants.Database.MEETINGS_PATH).child(courseId).child(id)
            .setValue(meetingDto.toMap()).await()
        return get(courseId, id)
    }

    override suspend fun delete(courseId: String, id: String) {
        database.child(FirebaseConstants.Database.MEETINGS_PATH).child(courseId).child(id)
            .removeValue().await()
    }

    override suspend fun get(courseId: String, id: String): MeetingDto? {
        val documentSnapshot =
            database.child(FirebaseConstants.Database.MEETINGS_PATH).child(courseId).child(id)
                .get().await()
        return documentSnapshot.getValue<MeetingDto>()
    }

    override suspend fun list(
        courseId: String,
        meetingStates: List<MeetingState>,
        orderBy: String,
        pageSize: Int?
    ): List<MeetingDto> {
        // TODO("Use orderBy and pageSize")
        val meetings =
            database.child(FirebaseConstants.Database.MEETINGS_PATH).child(courseId)
                .orderByChild(FirebaseConstants.Database.CREATION_TIME).get()
                .await().children.mapNotNull { snapshot -> snapshot.getValue<MeetingDto>() }
        val newMeetings = if (meetingStates.isNotEmpty()) {
            meetings.filter { dto ->
                meetingStates.any { state ->
                    state == dto.state
                }
            }
        } else {
            meetings
        }
        return newMeetings
    }

    override suspend fun patch(courseId: String, id: String, meetingDto: MeetingDto): MeetingDto? {
        database.child(FirebaseConstants.Database.MEETINGS_PATH).child(courseId).child(id)
            .updateChildren(meetingDto.toMap()).await()
        return get(courseId, id)
    }
}