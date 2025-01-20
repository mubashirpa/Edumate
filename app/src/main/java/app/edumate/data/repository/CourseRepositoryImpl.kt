package app.edumate.data.repository

import app.edumate.core.Supabase
import app.edumate.data.mapper.toCourseDto
import app.edumate.data.remote.dto.courses.CourseDto
import app.edumate.domain.model.Course
import app.edumate.domain.repository.CourseRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns

class CourseRepositoryImpl(
    private val postgrest: Postgrest,
) : CourseRepository {
    override suspend fun createCourse(course: Course): CourseDto {
        val courseDto = course.toCourseDto()
        return postgrest
            .from(Supabase.Table.COURSES)
            .insert(courseDto) {
                select()
            }.decodeSingle()
    }

    override suspend fun getCourses(userId: String): List<CourseDto> =
        postgrest
            .from(Supabase.Table.MEMBERS)
            .select(Columns.raw("*, course:courses(*), users(*)")) {
                filter {
                    eq(Supabase.Column.USER_ID, userId)
                }
            }.decodeList()

    override suspend fun getCourse(id: String): CourseDto? =
        postgrest[Supabase.Table.COURSES]
            .select(Columns.raw("*, owner:users!ownerId(*)")) {
                filter {
                    eq(Supabase.Column.ID, id)
                }
            }.decodeSingleOrNull()

    override suspend fun updateCourse(
        id: String,
        name: String?,
        room: String?,
        section: String?,
        subject: String?,
    ): CourseDto? =
        postgrest[Supabase.Table.COURSES]
            .update(
                {
                    name?.let {
                        set(Supabase.Column.NAME, name)
                    }
                    set(Supabase.Column.ROOM, room)
                    set(Supabase.Column.SECTION, section)
                    set(Supabase.Column.SUBJECT, subject)
                },
            ) {
                select()
                filter {
                    eq(Supabase.Column.ID, id)
                }
            }.decodeSingleOrNull()

    override suspend fun deleteCourse(id: String): CourseDto? =
        postgrest[Supabase.Table.COURSES]
            .delete {
                select()
                filter {
                    eq(Supabase.Column.ID, id)
                }
            }.decodeSingleOrNull()
}
