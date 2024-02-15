package edumate.app.data.repository

import edumate.app.core.Server
import edumate.app.data.remote.dto.classroom.studentSubmissions.StudentSubmission
import edumate.app.data.remote.dto.classroom.studentSubmissions.StudentSubmissionsDto
import edumate.app.data.remote.dto.classroom.studentSubmissions.SubmissionState
import edumate.app.domain.repository.LateValues
import edumate.app.domain.repository.StudentSubmissionRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import javax.inject.Inject

class StudentSubmissionRepositoryImpl
    @Inject
    constructor(
        private val httpClient: HttpClient,
    ) : StudentSubmissionRepository {
        override suspend fun get(
            courseId: String,
            courseWorkId: String,
            id: String,
        ): StudentSubmission {
            return httpClient.get(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_COURSE_WORK)
                    appendPathSegments(courseWorkId)
                    appendPathSegments(Server.ENDPOINT_STUDENT_SUBMISSIONS)
                    appendPathSegments(id)
                }
            }.body()
        }

        override suspend fun list(
            courseId: String,
            courseWorkId: String,
            userId: String?,
            states: List<SubmissionState>?,
            late: LateValues?,
            pageSize: Int?,
            pageToken: String?,
        ): StudentSubmissionsDto {
            return httpClient.get(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_COURSE_WORK)
                    appendPathSegments(courseWorkId)
                    appendPathSegments(Server.ENDPOINT_STUDENT_SUBMISSIONS)
                    if (late != null) {
                        parameters.append(Server.Parameters.LATE, late.name)
                    }
                    if (pageSize != null) {
                        parameters.append(Server.Parameters.PAGE_SIZE, pageSize.toString())
                    }
                    if (pageToken != null) {
                        parameters.append(Server.Parameters.PAGE_TOKEN, pageToken)
                    }
                    states?.forEach { state ->
                        parameters.append(Server.Parameters.STATES, state.name)
                    }
                    if (userId != null) {
                        parameters.append(Server.Parameters.USER_ID, userId)
                    }
                }
            }.body()
        }

        override suspend fun update(
            courseId: String,
            courseWorkId: String,
            id: String,
            studentSubmission: StudentSubmission,
        ): StudentSubmission {
            return httpClient.put(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_COURSE_WORK)
                    appendPathSegments(courseWorkId)
                    appendPathSegments(Server.ENDPOINT_STUDENT_SUBMISSIONS)
                    appendPathSegments(id)
                }
                contentType(ContentType.Application.Json)
                setBody(studentSubmission)
            }.body()
        }

        override suspend fun reclaim(
            courseId: String,
            courseWorkId: String,
            id: String,
        ) {
            httpClient.post(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_COURSE_WORK)
                    appendPathSegments(courseWorkId)
                    appendPathSegments(Server.ENDPOINT_STUDENT_SUBMISSIONS)
                    appendPathSegments("$id:${Server.Parameters.RECLAIM}")
                }
            }
        }

        override suspend fun `return`(
            courseId: String,
            courseWorkId: String,
            id: String,
        ) {
            httpClient.post(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_COURSE_WORK)
                    appendPathSegments(courseWorkId)
                    appendPathSegments(Server.ENDPOINT_STUDENT_SUBMISSIONS)
                    appendPathSegments("$id:${Server.Parameters.RETURN}")
                }
            }
        }

        override suspend fun turnIn(
            courseId: String,
            courseWorkId: String,
            id: String,
        ) {
            httpClient.post(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_COURSE_WORK)
                    appendPathSegments(courseWorkId)
                    appendPathSegments(Server.ENDPOINT_STUDENT_SUBMISSIONS)
                    appendPathSegments("$id:${Server.Parameters.TURN_IN}")
                }
            }
        }
    }
