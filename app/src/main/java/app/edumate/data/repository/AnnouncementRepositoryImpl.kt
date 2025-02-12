package app.edumate.data.repository

import app.edumate.core.Supabase
import app.edumate.data.remote.dto.announcement.AnnouncementDto
import app.edumate.data.remote.dto.comment.CommentDto
import app.edumate.data.remote.dto.material.MaterialDto
import app.edumate.domain.repository.AnnouncementRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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
                order(Supabase.Column.CREATION_TIME, Order.DESCENDING)
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

    override suspend fun createComment(
        courseId: String,
        announcementId: String,
        userId: String,
        text: String,
    ): CommentDto =
        postgrest
            .rpc(
                function = Supabase.Function.INSERT_ANNOUNCEMENT_COMMENT,
                parameters =
                    buildJsonObject {
                        put(Supabase.Parameter.COURSE_ID, courseId)
                        put(Supabase.Parameter.ANNOUNCEMENT_ID, announcementId)
                        put(Supabase.Parameter.USER_ID, userId)
                        put(Supabase.Parameter.TEXT, text)
                    },
            ).decodeAs()

    override suspend fun getComments(announcementId: String): List<CommentDto> =
        postgrest
            .rpc(
                function = Supabase.Function.GET_ANNOUNCEMENT_COMMENTS,
                parameters =
                    buildJsonObject {
                        put(Supabase.Parameter.ANNOUNCEMENT_ID, announcementId)
                    },
            ).decodeList()
}
