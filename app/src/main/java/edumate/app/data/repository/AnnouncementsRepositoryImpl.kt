package edumate.app.data.repository

import edumate.app.data.remote.dto.AnnouncementDto
import edumate.app.domain.model.announcements.AnnouncementState
import edumate.app.domain.model.announcements.AssigneeMode
import edumate.app.domain.model.announcements.IndividualStudentsOptions
import edumate.app.domain.repository.AnnouncementsRepository

class AnnouncementsRepositoryImpl : AnnouncementsRepository {

    override suspend fun create(
        courseId: String,
        announcementDto: AnnouncementDto
    ): AnnouncementDto? {
        TODO("Not yet implemented")
    }

    override suspend fun delete(courseId: String, id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun get(courseId: String, id: String): AnnouncementDto? {
        TODO("Not yet implemented")
    }

    override suspend fun list(
        courseId: String,
        announcementState: AnnouncementState,
        orderBy: String,
        pageSize: Int?
    ): List<AnnouncementDto> {
        // TODO("Not yet implemented")
        return emptyList()
    }

    override suspend fun modifyAssignees(
        courseId: String,
        id: String,
        assigneeMode: AssigneeMode,
        modifyIndividualStudentsOptions: IndividualStudentsOptions?
    ): AnnouncementDto? {
        TODO("Not yet implemented")
    }

    override suspend fun patch(
        courseId: String,
        id: String,
        announcementDto: AnnouncementDto
    ): AnnouncementDto? {
        TODO("Not yet implemented")
    }
}