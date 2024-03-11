package edumate.app.domain.repository

import edumate.app.data.remote.dto.classroom.announcements.Announcement
import edumate.app.data.remote.dto.classroom.announcements.AnnouncementState
import edumate.app.data.remote.dto.classroom.announcements.AnnouncementsDto

interface AnnouncementsRepository {
    /**
     * Creates an announcement.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param announcement An instance of [Announcement].
     * @return If successful, the response body contains a newly created instance of [Announcement].
     */
    suspend fun create(
        accessToken: String,
        courseId: String,
        announcement: Announcement,
    ): Announcement

    /**
     * Deletes an announcement.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param id Identifier of the announcement to delete.
     */
    suspend fun delete(
        accessToken: String,
        courseId: String,
        id: String,
    )

    /**
     * Returns an announcement.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param id Identifier of the announcement.
     * @return If successful, the response body contains an instance of [Announcement].
     */
    suspend fun get(
        accessToken: String,
        courseId: String,
        id: String,
    ): Announcement

    /**
     * Returns a list of announcements that the requester is permitted to view.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param announcementStates Restriction on the state of announcements returned. If this
     * argument is left unspecified, the default value is PUBLISHED.
     * @param orderBy Optional sort ordering for results. Supported field is updateTime. Supported
     * direction keywords are asc and desc. If not specified, updateTime desc is the default
     * behavior. Examples: updateTime asc, updateTime.
     * @param pageSize Maximum number of items to return.
     * @param page nextPage value returned from a previous list call, indicating that the subsequent
     * page of results should be returned.
     * @return If successful, the response body contains an instance of [AnnouncementsDto].
     */
    suspend fun list(
        accessToken: String,
        courseId: String,
        announcementStates: List<AnnouncementState>? = listOf(AnnouncementState.PUBLISHED),
        orderBy: String? = "updateTime desc",
        pageSize: Int? = null,
        page: Int? = null,
    ): AnnouncementsDto

    /**
     * Updates one or more fields of an announcement.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param id Identifier of the announcement.
     * @param updateMask Mask that identifies which fields on the announcement to update.
     * @param announcement An instance of [Announcement].
     * @return If successful, the response body contains an instance of [Announcement].
     */
    suspend fun patch(
        accessToken: String,
        courseId: String,
        id: String,
        updateMask: String,
        announcement: Announcement,
    ): Announcement
}
