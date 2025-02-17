package app.edumate.data.repository

import app.edumate.core.Supabase
import app.edumate.data.remote.dto.announcement.AnnouncementDto
import app.edumate.data.remote.dto.comment.CommentDto
import app.edumate.data.remote.dto.material.MaterialDto
import app.edumate.domain.repository.AnnouncementRepository
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.datetime.LocalDateTime
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
            .rpc(
                function = Supabase.Function.GET_ANNOUNCEMENTS,
                parameters =
                    buildJsonObject {
                        put(Supabase.Parameter.COURSE_ID, courseId)
                    },
            ).decodeList()

    override suspend fun updateAnnouncement(
        id: String,
        text: String?,
        materials: List<MaterialDto>?,
        pinned: Boolean?,
        updateTime: LocalDateTime?,
    ): AnnouncementDto =
        postgrest[Supabase.Table.ANNOUNCEMENTS]
            .update(
                {
                    text?.let { set(Supabase.Column.TEXT, text) }
                    materials?.let {
                        set(
                            Supabase.Column.MATERIALS,
                            materials.takeIf { it.isNotEmpty() },
                        )
                    }
                    pinned?.let { set(Supabase.Column.PINNED, pinned) }
                    updateTime?.let { set(Supabase.Column.UPDATE_TIME, updateTime) }
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
