package app.edumate.data.repository

import app.edumate.core.Supabase
import app.edumate.data.remote.dto.announcement.AnnouncementDto
import app.edumate.domain.repository.AnnouncementRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns

class AnnouncementRepositoryImpl(
    private val postgrest: Postgrest,
) : AnnouncementRepository {
    override suspend fun getAnnouncements(courseId: String): List<AnnouncementDto> =
        postgrest
            .from(Supabase.Table.ANNOUNCEMENTS)
            .select(Columns.raw("*, creator:users!creator_user_id(*)")) {
                filter {
                    eq(Supabase.Column.COURSE_ID, courseId)
                }
            }.decodeList()
}
