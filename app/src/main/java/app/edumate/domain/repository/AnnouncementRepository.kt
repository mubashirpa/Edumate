package app.edumate.domain.repository

import app.edumate.data.remote.dto.announcement.AnnouncementDto

interface AnnouncementRepository {
    suspend fun getAnnouncements(courseId: String): List<AnnouncementDto>
}
