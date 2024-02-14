package edumate.app.data.repository

import edumate.app.core.Server
import edumate.app.data.remote.dto.courses.Course
import edumate.app.data.remote.dto.courses.CoursesDto
import edumate.app.domain.model.courses.CourseState
import edumate.app.domain.repository.CoursesRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import javax.inject.Inject

class CoursesRepositoryImpl
    @Inject
    constructor(
        private val httpClient: HttpClient,
    ) : CoursesRepository {
        override suspend fun create(course: Course): Course? {
            return httpClient.post(Server.API_BASE_URL) {
                url { appendPathSegments(Server.ENDPOINT_COURSES) }
                contentType(ContentType.Application.Json)
                setBody(course)
            }.body()
        }

        override suspend fun delete(id: String) {
            httpClient.delete(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(id)
                }
            }
        }

        override suspend fun get(id: String): Course? {
            return httpClient.get(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(id)
                }
            }.body()
        }

        override suspend fun list(
            courseStates: List<CourseState>,
            pageSize: Int?,
            pageToken: String?,
            studentId: String?,
            teacherId: String?,
        ): CoursesDto {
            return httpClient.get(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    courseStates.forEach { courseState ->
                        parameters.append(Server.Parameters.COURSE_STATES, courseState.name)
                    }
                    if (pageSize != null) {
                        parameters.append(Server.Parameters.PAGE_SIZE, pageSize.toString())
                    }
                    if (pageToken != null) {
                        parameters.append(Server.Parameters.PAGE_TOKEN, pageToken)
                    }
                    if (studentId != null) {
                        parameters.append(Server.Parameters.STUDENT_ID, studentId)
                    }
                    if (teacherId != null) {
                        parameters.append(Server.Parameters.TEACHER_ID, teacherId)
                    }
                }
            }.body()
        }

        override suspend fun update(
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
            }.body()
        }
    }
