package app.edumate.data.repository

import app.edumate.core.Supabase
import app.edumate.data.mapper.toCourseDto
import app.edumate.data.remote.dto.courses.CourseDto
import app.edumate.data.remote.dto.courses.CoursesDto
import app.edumate.domain.model.courses.Course
import app.edumate.domain.repository.CourseRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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

    override suspend fun getCourses(userId: String): List<CoursesDto>? =
        postgrest
            .from(Supabase.Table.MEMBERS)
            .select(Columns.raw("role, course:courses(*, owner:users!owner_id(*))")) {
                filter {
                    eq(Supabase.Column.USER_ID, userId)
                }
            }.decodeList()

    override suspend fun getCourse(id: String): CourseDto? =
        postgrest[Supabase.Table.COURSES]
            .select(Columns.raw("*, owner:users!owner_id(*)")) {
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
                    name?.let { set(Supabase.Column.NAME, it) }
                    set(Supabase.Column.ROOM, room)
                    set(Supabase.Column.SECTION, section)
                    set(Supabase.Column.SUBJECT, subject)

                    val now: Instant = Clock.System.now()
                    val updateTime = now.toLocalDateTime(TimeZone.UTC)
                    set(Supabase.Column.UPDATE_TIME, updateTime)
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

    override suspend fun joinCourse(
        courseId: String,
        userId: String,
    ): CourseDto? =
        postgrest
            .rpc(
                function = Supabase.Function.INSERT_MEMBER,
                parameters =
                    buildJsonObject {
                        put(Supabase.Column.COURSE_ID, courseId)
                        put(Supabase.Column.USER_ID, userId)
                    },
            ).decodeSingleOrNull()
}
