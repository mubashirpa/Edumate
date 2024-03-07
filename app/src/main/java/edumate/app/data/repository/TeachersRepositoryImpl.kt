package edumate.app.data.repository

import edumate.app.core.Server
import edumate.app.data.remote.dto.classroom.teachers.Teacher
import edumate.app.data.remote.dto.classroom.teachers.TeachersDto
import edumate.app.domain.repository.TeachersRepository
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

class TeachersRepositoryImpl
    @Inject
    constructor(
        private val httpClient: HttpClient,
    ) : TeachersRepository {
        override suspend fun create(
            accessToken: String,
            courseId: String,
            teacher: Teacher,
        ): Teacher {
            return httpClient.post(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_TEACHERS)
                }
                contentType(ContentType.Application.Json)
                setBody(teacher)
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
                    appendPathSegments(Server.ENDPOINT_TEACHERS)
                    appendPathSegments(userId)
                }
                header(HttpHeaders.Authorization, accessToken)
            }
        }

        override suspend fun get(
            accessToken: String,
            courseId: String,
            userId: String,
        ): Teacher {
            return httpClient.get(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_TEACHERS)
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
        ): TeachersDto {
            return httpClient.get(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_TEACHERS)
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
