package app.edumate.data.repository

import app.edumate.core.Supabase
import app.edumate.data.remote.dto.courseWork.CourseWorkTypeDto
import app.edumate.data.remote.dto.studentSubmission.AssignmentSubmissionDto
import app.edumate.data.remote.dto.studentSubmission.StudentSubmissionDto
import app.edumate.data.remote.dto.studentSubmission.SubmissionStateDto
import app.edumate.domain.repository.StudentSubmissionRepository
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class StudentSubmissionRepositoryImpl(
    private val postgrest: Postgrest,
) : StudentSubmissionRepository {
    override suspend fun getStudentSubmission(
        courseId: String,
        courseWorkId: String,
        userId: String,
        courseWorkType: CourseWorkTypeDto,
    ): StudentSubmissionDto =
        postgrest
            .rpc(
                function = Supabase.Function.GET_STUDENT_SUBMISSION,
                parameters =
                    buildJsonObject {
                        put(Supabase.Parameters.COURSE_ID, courseId)
                        put(Supabase.Parameters.COURSE_WORK_ID, courseWorkId)
                        put(Supabase.Parameters.USER_ID, userId)
                        put(Supabase.Parameters.COURSE_WORK_TYPE, courseWorkType.name)
                    },
            ).decodeAs()

    override suspend fun modifyStudentSubmissionAttachments(
        id: String,
        attachments: AssignmentSubmissionDto?,
    ): StudentSubmissionDto =
        postgrest[Supabase.Table.STUDENT_SUBMISSIONS]
            .update(
                {
                    set(Supabase.Column.ASSIGNMENT_SUBMISSION, attachments)
                },
            ) {
                select()
                filter {
                    eq(Supabase.Column.ID, id)
                }
            }.decodeSingle()

    override suspend fun updateStudentSubmission(
        id: String,
        updates: StudentSubmissionDto,
    ): StudentSubmissionDto =
        postgrest[Supabase.Table.STUDENT_SUBMISSIONS]
            .update(
                {
                    updates.multipleChoiceSubmission?.let {
                        set(Supabase.Column.MULTIPLE_CHOICE_SUBMISSION, it)
                    }
                    updates.shortAnswerSubmission?.let {
                        set(Supabase.Column.SHORT_ANSWER_SUBMISSION, it)
                    }
                    updates.assignedGrade?.let {
                        set(Supabase.Column.ASSIGNED_GRADE, it)
                    }

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

    override suspend fun reclaimStudentSubmission(id: String) {
        postgrest[Supabase.Table.STUDENT_SUBMISSIONS]
            .update(
                {
                    set(Supabase.Column.STATE, SubmissionStateDto.RECLAIMED_BY_STUDENT)
                },
            ) {
                filter {
                    eq(Supabase.Column.ID, id)
                }
            }
    }

    override suspend fun returnStudentSubmission(id: String) {
        postgrest[Supabase.Table.STUDENT_SUBMISSIONS]
            .update(
                {
                    set(Supabase.Column.STATE, SubmissionStateDto.RETURNED)
                },
            ) {
                filter {
                    eq(Supabase.Column.ID, id)
                }
            }
    }

    override suspend fun turnInStudentSubmission(
        courseWorkId: String,
        id: String,
    ) {
        postgrest
            .rpc(
                function = Supabase.Function.TURN_IN_STUDENT_SUBMISSION,
                parameters =
                    buildJsonObject {
                        put(Supabase.Parameters.COURSE_WORK_ID, courseWorkId)
                        put(Supabase.Parameters.ID, id)
                    },
            )
    }
}
