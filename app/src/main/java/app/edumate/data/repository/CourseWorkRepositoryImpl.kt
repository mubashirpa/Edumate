package app.edumate.data.repository

import app.edumate.core.Supabase
import app.edumate.data.remote.dto.courseWork.CourseWorkDto
import app.edumate.data.remote.dto.courseWork.MultipleChoiceQuestionDto
import app.edumate.data.remote.dto.material.MaterialDto
import app.edumate.domain.repository.CourseWorkRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class CourseWorkRepositoryImpl(
    private val postgrest: Postgrest,
) : CourseWorkRepository {
    override suspend fun createCourseWork(courseWork: CourseWorkDto): CourseWorkDto =
        postgrest
            .from(Supabase.Table.COURSE_WORKS)
            .insert(courseWork) {
                select()
            }.decodeSingle()

    override suspend fun getCourseWorks(courseId: String): List<CourseWorkDto> =
        postgrest
            .from(Supabase.Table.COURSE_WORKS)
            .select(Columns.raw("*")) {
                filter {
                    eq(Supabase.Column.COURSE_ID, courseId)
                }
            }.decodeList()

    override suspend fun getCourseWork(id: String): CourseWorkDto? =
        postgrest[Supabase.Table.COURSE_WORKS]
            .select(Columns.raw("*")) {
                filter {
                    eq(Supabase.Column.ID, id)
                }
            }.decodeSingleOrNull()

    override suspend fun updateCourseWork(
        id: String,
        title: String,
        description: String?,
        multipleChoiceQuestion: MultipleChoiceQuestionDto?,
        materials: List<MaterialDto>?,
        maxPoints: Int?,
        dueTime: String?,
    ): CourseWorkDto =
        postgrest[Supabase.Table.COURSE_WORKS]
            .update(
                {
                    set(Supabase.Column.TITLE, title)
                    set(Supabase.Column.DESCRIPTION, description)
                    set(Supabase.Column.MULTIPLE_CHOICE_QUESTION, multipleChoiceQuestion)
                    set(Supabase.Column.MATERIALS, materials)
                    set(Supabase.Column.MAX_POINTS, maxPoints)
                    set(Supabase.Column.DUE_TIME, dueTime)

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

    override suspend fun deleteCourseWork(id: String): CourseWorkDto =
        postgrest[Supabase.Table.COURSE_WORKS]
            .delete {
                select()
                filter {
                    eq(Supabase.Column.ID, id)
                }
            }.decodeSingle()
}
