package app.edumate.data.repository

import app.edumate.core.Supabase
import app.edumate.data.remote.dto.comment.CommentsDto
import app.edumate.domain.repository.CommentRepository
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class CommentRepositoryImpl(
    private val postgrest: Postgrest,
) : CommentRepository {
    override suspend fun updateComment(
        commentId: String,
        text: String,
    ): CommentsDto =
        postgrest[Supabase.Table.COMMENTS]
            .update(
                {
                    set(Supabase.Column.TEXT, text)

                    val now: Instant = Clock.System.now()
                    val updateTime = now.toLocalDateTime(TimeZone.UTC)
                    set(Supabase.Column.UPDATE_TIME, updateTime)
                },
            ) {
                select()
                filter {
                    eq(Supabase.Column.ID, commentId)
                }
            }.decodeSingle()

    override suspend fun deleteComment(commentId: String): CommentsDto =
        postgrest[Supabase.Table.COMMENTS]
            .delete {
                select()
                filter {
                    eq(Supabase.Column.ID, commentId)
                }
            }.decodeSingle()
}
