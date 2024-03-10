package edumate.app.data.repository

import edumate.app.core.Server
import edumate.app.data.remote.dto.classroom.courseWork.CourseWork
import edumate.app.data.remote.dto.classroom.courseWork.CourseWorkDto
import edumate.app.data.remote.dto.classroom.courseWork.CourseWorkState
import edumate.app.domain.repository.CourseWorkRepository
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

class CourseWorkRepositoryImpl
    @Inject
    constructor(
        private val httpClient: HttpClient,
    ) : CourseWorkRepository {
        override suspend fun create(
            accessToken: String,
            courseId: String,
            courseWork: CourseWork,
        ): CourseWork {
            return httpClient.post(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_COURSE_WORK)
                }
                contentType(ContentType.Application.Json)
                setBody(courseWork)
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }

        override suspend fun delete(
            accessToken: String,
            courseId: String,
            id: String,
        ) {
            httpClient.delete(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_COURSE_WORK)
                    appendPathSegments(id)
                }
                header(HttpHeaders.Authorization, accessToken)
            }
        }

        override suspend fun get(
            accessToken: String,
            courseId: String,
            id: String,
        ): CourseWork {
            return httpClient.get(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_COURSE_WORK)
                    appendPathSegments(id)
                }
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }

        override suspend fun list(
            accessToken: String,
            courseId: String,
            courseWorkStates: List<CourseWorkState>?,
            orderBy: String?,
            pageSize: Int?,
            pageToken: String?,
        ): CourseWorkDto {
            return httpClient.get(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_COURSE_WORK)
                    courseWorkStates?.forEach { state ->
                        parameters.append(Server.Parameters.COURSE_WORK_STATES, state.name)
                    }
                    if (orderBy != null) {
                        parameters.append(Server.Parameters.ORDER_BY, orderBy)
                    }
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

        override suspend fun update(
            accessToken: String,
            courseId: String,
            id: String,
            courseWork: CourseWork,
        ): CourseWork {
            return httpClient.put(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_COURSE_WORK)
                    appendPathSegments(id)
                }
                contentType(ContentType.Application.Json)
                setBody(courseWork)
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }
    }
