package app.edumate.data.repository

import app.edumate.core.Supabase
import app.edumate.data.remote.dto.courseWork.CourseWorkTypeDto
import app.edumate.data.remote.dto.studentSubmission.StudentSubmissionDto
import app.edumate.domain.repository.StudentSubmissionRepository
import io.github.jan.supabase.postgrest.Postgrest
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
}
