package edumate.app.data.repository

import edumate.app.core.Server
import edumate.app.data.remote.dto.classroom.students.Student
import edumate.app.data.remote.dto.classroom.students.StudentsDto
import edumate.app.domain.repository.StudentsRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import javax.inject.Inject

class StudentsRepositoryImpl
    @Inject
    constructor(
        private val httpClient: HttpClient,
    ) : StudentsRepository {
        override suspend fun create(
            accessToken: String,
            courseId: String,
            enrollmentCode: String?,
            student: Student,
        ): Student {
            return httpClient.post(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_STUDENTS)
                    if (enrollmentCode != null) {
                        parameters.append(Server.Parameters.ENROLLMENT_CODE, enrollmentCode)
                    }
                }
                contentType(ContentType.Application.Json)
                setBody(student)
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }

        override suspend fun delete(
            accessToken: String,
            courseId: String,
            userId: String,
        ) {
            httpClient.delete(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_STUDENTS)
                    appendPathSegments(userId)
                }
                header(HttpHeaders.Authorization, accessToken)
            }
        }

        override suspend fun get(
            accessToken: String,
            courseId: String,
            userId: String,
        ): Student {
            return httpClient.get(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_STUDENTS)
                    appendPathSegments(userId)
                }
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }

        override suspend fun list(
            accessToken: String,
            courseId: String,
            pageSize: Int?,
            pageToken: String?,
        ): StudentsDto {
            return httpClient.get(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_STUDENTS)
                    if (pageSize != null) {
                        parameters.append(Server.Parameters.PAGE_SIZE, pageSize.toString())
                    }
                    if (pageToken != null) {
                        parameters.append(Server.Parameters.PAGE_TOKEN, pageToken)
                    }
                }
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }
    }
