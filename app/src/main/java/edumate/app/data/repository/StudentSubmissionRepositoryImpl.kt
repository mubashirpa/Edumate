package edumate.app.data.repository

import edumate.app.core.Server
import edumate.app.data.remote.dto.classroom.studentSubmissions.ModifyAttachments
import edumate.app.data.remote.dto.classroom.studentSubmissions.StudentSubmission
import edumate.app.data.remote.dto.classroom.studentSubmissions.StudentSubmissionsDto
import edumate.app.data.remote.dto.classroom.studentSubmissions.SubmissionState
import edumate.app.domain.repository.LateValues
import edumate.app.domain.repository.StudentSubmissionRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import javax.inject.Inject

class StudentSubmissionRepositoryImpl
    @Inject
    constructor(
        private val httpClient: HttpClient,
    ) : StudentSubmissionRepository {
        override suspend fun get(
            accessToken: String,
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
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }

        override suspend fun list(
            accessToken: String,
            courseId: String,
            courseWorkId: String,
            late: LateValues?,
            pageSize: Int?,
            page: Int?,
            states: List<SubmissionState>?,
            userId: String?,
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
                    if (page != null) {
                        parameters.append(Server.Parameters.PAGE, page.toString())
                    }
                    states?.forEach { state ->
                        parameters.append(Server.Parameters.STATES, state.name)
                    }
                    if (userId != null) {
                        parameters.append(Server.Parameters.USER_ID, userId)
                    }
                }
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }

        override suspend fun modifyAttachments(
            accessToken: String,
            courseId: String,
            courseWorkId: String,
            id: String,
            modifyAttachments: ModifyAttachments,
        ): StudentSubmission {
            return httpClient.post(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_COURSE_WORK)
                    appendPathSegments(courseWorkId)
                    appendPathSegments(Server.ENDPOINT_STUDENT_SUBMISSIONS)
                    appendPathSegments("$id:${Server.Parameters.MODIFY_ATTACHMENTS}")
                }
                contentType(ContentType.Application.Json)
                setBody(modifyAttachments)
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }

        override suspend fun patch(
            accessToken: String,
            courseId: String,
            courseWorkId: String,
            id: String,
            updateMask: String,
            studentSubmission: StudentSubmission,
        ): StudentSubmission {
            return httpClient.patch(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_COURSE_WORK)
                    appendPathSegments(courseWorkId)
                    appendPathSegments(Server.ENDPOINT_STUDENT_SUBMISSIONS)
                    appendPathSegments(id)
                    parameters.append(Server.Parameters.UPDATE_MASK, updateMask)
                }
                contentType(ContentType.Application.Json)
                setBody(studentSubmission)
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }

        override suspend fun reclaim(
            accessToken: String,
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
                header(HttpHeaders.Authorization, accessToken)
            }
        }

        override suspend fun `return`(
            accessToken: String,
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
                header(HttpHeaders.Authorization, accessToken)
            }
        }

        override suspend fun turnIn(
            accessToken: String,
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
                header(HttpHeaders.Authorization, accessToken)
            }
        }
    }
