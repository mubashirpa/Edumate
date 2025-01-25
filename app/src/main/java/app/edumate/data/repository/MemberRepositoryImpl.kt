package app.edumate.data.repository

import app.edumate.core.Supabase
import app.edumate.domain.repository.MemberRepository
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class MemberRepositoryImpl(
    private val postgrest: Postgrest,
) : MemberRepository {
    override suspend fun insertMember(
        courseId: String,
        userId: String,
    ) {
        postgrest
            .rpc(
                function = Supabase.Function.INSERT_MEMBER,
                parameters =
                    buildJsonObject {
                        put(Supabase.Column.COURSE_ID, courseId)
                        put(Supabase.Column.USER_ID, userId)
                    },
            )
    }

    override suspend fun deleteMember(
        courseId: String,
        userId: String,
    ) {
        postgrest[Supabase.Table.MEMBERS]
            .delete {
                filter {
                    eq(Supabase.Column.COURSE_ID, courseId)
                    eq(Supabase.Column.USER_ID, userId)
                }
            }
    }
}
