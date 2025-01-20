package app.edumate.data.repository

import app.edumate.core.Constants
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
            .from(Constants.Table.COURSES)
            .insert(courseDto) {
                select()
            }.decodeSingle()
    }

    override suspend fun getCourses(userId: String): List<CourseDto> =
        postgrest
            .from(Constants.Table.MEMBERS)
            .select(Columns.raw("*, course:courses(*), users(*)")) {
                filter {
                    eq(Constants.Column.USER_ID, userId)
                }
            }.decodeList()

    override suspend fun getCourse(id: String): CourseDto? =
        postgrest[Constants.Table.COURSES]
            .select(Columns.raw("*, owner:users!ownerId(*)")) {
                filter {
                    eq(Constants.Column.ID, id)
                }
            }.decodeSingleOrNull()

    override suspend fun updateCourse(
        id: String,
        name: String?,
        room: String?,
        section: String?,
        subject: String?,
    ): CourseDto? =
        postgrest[Constants.Table.COURSES]
            .update(
                {
                    name?.let {
                        set(Constants.Column.NAME, name)
                    }
                    set(Constants.Column.ROOM, room)
                    set(Constants.Column.SECTION, section)
                    set(Constants.Column.SUBJECT, subject)
                },
            ) {
                select()
                filter {
                    eq(Constants.Column.ID, id)
                }
            }.decodeSingleOrNull()

    override suspend fun deleteCourse(id: String): CourseDto? =
        postgrest[Constants.Table.COURSES]
            .delete {
                select()
                filter {
                    eq(Constants.Column.ID, id)
                }
            }.decodeSingleOrNull()
}
