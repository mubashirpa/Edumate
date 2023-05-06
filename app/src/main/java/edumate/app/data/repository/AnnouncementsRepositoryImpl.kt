package edumate.app.data.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import edumate.app.core.FirebaseConstants
import edumate.app.data.remote.dto.AnnouncementDto
import edumate.app.domain.model.AssigneeMode
import edumate.app.domain.model.IndividualStudentsOptions
import edumate.app.domain.model.announcements.AnnouncementState
import edumate.app.domain.repository.AnnouncementsRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class AnnouncementsRepositoryImpl @Inject constructor(
    private val database: DatabaseReference
) : AnnouncementsRepository {

    override suspend fun create(
        courseId: String,
        announcementDto: AnnouncementDto
    ): AnnouncementDto? {
        val id = announcementDto.id
        database.child(FirebaseConstants.Database.ANNOUNCEMENTS_PATH).child(courseId).child(id)
            .setValue(announcementDto.toMap()).await()
        return get(courseId, id)
    }

    override suspend fun delete(courseId: String, id: String) {
        database.child(FirebaseConstants.Database.ANNOUNCEMENTS_PATH).child(courseId).child(id)
            .removeValue().await()
    }

    override suspend fun get(courseId: String, id: String): AnnouncementDto? {
        val documentSnapshot =
            database.child(FirebaseConstants.Database.ANNOUNCEMENTS_PATH).child(courseId).child(id)
                .get().await()
        return documentSnapshot.getValue<AnnouncementDto>()
    }

    override suspend fun list(
        courseId: String,
        announcementState: AnnouncementState,
        orderBy: String,
        pageSize: Int?
    ): List<AnnouncementDto> {
        // TODO("Use announcementState, orderBy and pageSize")
        val announcements =
            database.child(FirebaseConstants.Database.ANNOUNCEMENTS_PATH).child(courseId)
                .orderByChild(FirebaseConstants.Database.CREATION_TIME).get()
                .await().children.mapNotNull { snapshot -> snapshot.getValue<AnnouncementDto>() }
        return announcements.filter { it.state == AnnouncementState.PUBLISHED }
    }

    override suspend fun modifyAssignees(
        courseId: String,
        id: String,
        assigneeMode: AssigneeMode,
        modifyIndividualStudentsOptions: IndividualStudentsOptions?
    ): AnnouncementDto? {
        TODO("Feature is not available yet")
    }

    override suspend fun patch(
        courseId: String,
        id: String,
        announcementDto: AnnouncementDto
    ): AnnouncementDto? {
        database.child(FirebaseConstants.Database.ANNOUNCEMENTS_PATH).child(courseId).child(id)
            .updateChildren(announcementDto.toMap()).await()
        return get(courseId, id)
    }
}