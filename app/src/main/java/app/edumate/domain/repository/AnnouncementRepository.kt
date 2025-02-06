package app.edumate.domain.repository

import app.edumate.data.remote.dto.announcement.AnnouncementDto
import app.edumate.data.remote.dto.comment.CommentDto
import app.edumate.data.remote.dto.comment.CommentsDto
import app.edumate.data.remote.dto.material.MaterialDto

interface AnnouncementRepository {
    suspend fun createAnnouncement(announcement: AnnouncementDto): AnnouncementDto

    suspend fun getAnnouncements(courseId: String): List<AnnouncementDto>

    suspend fun updateAnnouncement(
        id: String,
        text: String,
        materials: List<MaterialDto>?,
    ): AnnouncementDto

    suspend fun deleteAnnouncement(id: String): AnnouncementDto

    suspend fun createComment(
        courseId: String,
        announcementId: String,
        userId: String,
        text: String,
    ): CommentDto

    suspend fun getComments(announcementId: String): List<CommentsDto>
}
