package edumate.app.data.repository

import edumate.app.core.Server
import edumate.app.data.remote.dto.classroom.courses.Course
import edumate.app.data.remote.dto.classroom.courses.CoursesDto
import edumate.app.domain.model.classroom.courses.CourseState
import edumate.app.domain.repository.CoursesRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import javax.inject.Inject

class CoursesRepositoryImpl
    @Inject
    constructor(
        private val httpClient: HttpClient,
    ) : CoursesRepository {
        override suspend fun create(
            accessToken: String,
            course: Course,
        ): Course? {
            return httpClient.post(Server.API_BASE_URL) {
                url { appendPathSegments(Server.ENDPOINT_COURSES) }
                contentType(ContentType.Application.Json)
                setBody(course)
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }

        override suspend fun delete(
            accessToken: String,
            id: String,
        ) {
            httpClient.delete(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(id)
                }
                header(HttpHeaders.Authorization, accessToken)
            }
        }

        override suspend fun get(
            accessToken: String,
            id: String,
        ): Course? {
            return httpClient.get(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(id)
                }
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }

        override suspend fun list(
            accessToken: String,
            courseStates: List<CourseState>?,
            pageSize: Int?,
            page: Int?,
            studentId: String?,
            teacherId: String?,
        ): CoursesDto {
            return httpClient.get(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    courseStates?.forEach { state ->
                        parameters.append(Server.Parameters.COURSE_STATES, state.name)
                    }
                    if (pageSize != null) {
                        parameters.append(Server.Parameters.PAGE_SIZE, pageSize.toString())
                    }
                    if (page != null) {
                        parameters.append(Server.Parameters.PAGE, page.toString())
                    }
                    if (studentId != null) {
                        parameters.append(Server.Parameters.STUDENT_ID, studentId)
                    }
                    if (teacherId != null) {
                        parameters.append(Server.Parameters.TEACHER_ID, teacherId)
                    }
                }
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }

        override suspend fun update(
            accessToken: String,
            id: String,
            course: Course,
        ) {
            return httpClient.put(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(id)
                }
                contentType(ContentType.Application.Json)
                setBody(course)
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }
    }
