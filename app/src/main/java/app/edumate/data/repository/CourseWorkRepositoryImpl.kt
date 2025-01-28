package app.edumate.data.repository

import app.edumate.core.Supabase
import app.edumate.data.remote.dto.courseWorks.CourseWorkDto
import app.edumate.domain.repository.CourseWorkRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns

class CourseWorkRepositoryImpl(
    private val postgrest: Postgrest,
) : CourseWorkRepository {
    override suspend fun getCourseWorks(courseId: String): List<CourseWorkDto> =
        postgrest
            .from(Supabase.Table.COURSE_WORKS)
            .select(Columns.raw("*")) {
                filter {
                    eq(Supabase.Column.COURSE_ID, courseId)
                }
            }.decodeList()
}
