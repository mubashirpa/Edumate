package edumate.app.domain.repository

import edumate.app.data.remote.dto.AnnouncementDto
import edumate.app.domain.model.announcements.AnnouncementState
import edumate.app.domain.model.announcements.AssigneeMode
import edumate.app.domain.model.announcements.AssigneeMode.INDIVIDUAL_STUDENTS
import edumate.app.domain.model.announcements.IndividualStudentsOptions

interface AnnouncementsRepository {

    /**
     * Creates an announcement.
     * @param courseId Identifier of the course.
     * @param announcementDto Instance of [AnnouncementDto].
     * @return If successful, the response body contains a newly created instance of [AnnouncementDto].
     */
    suspend fun create(courseId: String, announcementDto: AnnouncementDto): AnnouncementDto?

    /**
     * Deletes  an announcement.
     * @param courseId Identifier of the course.
     * @param id Identifier of the announcement to delete.
     */
    suspend fun delete(courseId: String, id: String)

    /**
     * Returns an announcement.
     * @param courseId Identifier of the course.
     * @param id Identifier of the announcement.
     * @return If successful, the response body contains an instance of [AnnouncementDto].
     */
    suspend fun get(courseId: String, id: String): AnnouncementDto?

    /**
     * Returns an announcement.
     * @param courseId Identifier of the course.
     * @param announcementState Restriction on the state of announcements returned.
     * @param orderBy Optional sort ordering for results. Supported field is updateTime. Supported direction keywords are asc and desc. If not specified, updateTime desc is the default behavior. Examples: updateTime asc, updateTime.
     * @param pageSize Maximum number of items to return.
     * @return If successful, the response body contains a list of [AnnouncementDto].
     */
    suspend fun list(
        courseId: String,
        announcementState: AnnouncementState = AnnouncementState.PUBLISHED,
        orderBy: String = "updateTime desc",
        pageSize: Int? = null
    ): List<AnnouncementDto>

    /**
     * Modifies assignee mode and options of an announcement.
     * @param courseId Identifier of the course.
     * @param id Identifier of the announcement.
     * @param assigneeMode Mode of the announcement describing whether it is accessible by all students or specified individual students.
     * @param modifyIndividualStudentsOptions Set which students can view or cannot view the announcement. Must be specified only when [assigneeMode] is [INDIVIDUAL_STUDENTS].
     * @return If successful, the response body contains an instance of [AnnouncementDto].
     */
    suspend fun modifyAssignees(
        courseId: String,
        id: String,
        assigneeMode: AssigneeMode,
        modifyIndividualStudentsOptions: IndividualStudentsOptions?
    ): AnnouncementDto?

    /**
     * Updates one or more fields of an announcement.
     * @param courseId Identifier of the course.
     * @param id Identifier of the announcement.
     * @param announcementDto Instance of [AnnouncementDto].
     * @return If successful, the response body contains an instance of [AnnouncementDto].
     */
    suspend fun patch(
        courseId: String,
        id: String,
        announcementDto: AnnouncementDto
    ): AnnouncementDto?
}