package app.edumate.data.repository

import app.edumate.core.Supabase
import app.edumate.data.remote.dto.announcement.AnnouncementDto
import app.edumate.data.remote.dto.material.MaterialDto
import app.edumate.domain.repository.AnnouncementRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AnnouncementRepositoryImpl(
    private val postgrest: Postgrest,
) : AnnouncementRepository {
    override suspend fun createAnnouncement(announcement: AnnouncementDto): AnnouncementDto =
        postgrest
            .from(Supabase.Table.ANNOUNCEMENTS)
            .insert(announcement) {
                select()
            }.decodeSingle()

    override suspend fun getAnnouncements(courseId: String): List<AnnouncementDto> =
        postgrest
            .from(Supabase.Table.ANNOUNCEMENTS)
            .select(Columns.raw("*, creator:users!creator_user_id(*)")) {
                filter {
                    eq(Supabase.Column.COURSE_ID, courseId)
                }
            }.decodeList()

    override suspend fun updateAnnouncement(
        id: String,
        text: String,
        materials: List<MaterialDto>?,
    ): AnnouncementDto =
        postgrest[Supabase.Table.ANNOUNCEMENTS]
            .update(
                {
                    set(Supabase.Column.TEXT, text)
                    set(Supabase.Column.MATERIALS, materials)

                    val now: Instant = Clock.System.now()
                    val updateTime = now.toLocalDateTime(TimeZone.UTC)
                    set(Supabase.Column.UPDATE_TIME, updateTime)
                },
            ) {
                select()
                filter {
                    eq(Supabase.Column.ID, id)
                }
            }.decodeSingle()

    override suspend fun deleteAnnouncement(id: String): AnnouncementDto =
        postgrest[Supabase.Table.ANNOUNCEMENTS]
            .delete {
                select()
                filter {
                    eq(Supabase.Column.ID, id)
                }
            }.decodeSingle()
}
