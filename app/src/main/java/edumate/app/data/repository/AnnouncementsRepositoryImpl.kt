package edumate.app.data.repository

import edumate.app.core.Server
import edumate.app.data.remote.dto.classroom.announcements.Announcement
import edumate.app.data.remote.dto.classroom.announcements.AnnouncementState
import edumate.app.data.remote.dto.classroom.announcements.AnnouncementsDto
import edumate.app.domain.repository.AnnouncementsRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
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

class AnnouncementsRepositoryImpl
    @Inject
    constructor(
        private val httpClient: HttpClient,
    ) : AnnouncementsRepository {
        override suspend fun create(
            accessToken: String,
            courseId: String,
            announcement: Announcement,
        ): Announcement {
            return httpClient.post(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_ANNOUNCEMENTS)
                }
                contentType(ContentType.Application.Json)
                setBody(announcement)
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
                    appendPathSegments(Server.ENDPOINT_ANNOUNCEMENTS)
                    appendPathSegments(id)
                }
                header(HttpHeaders.Authorization, accessToken)
            }
        }

        override suspend fun get(
            accessToken: String,
            courseId: String,
            id: String,
        ): Announcement {
            return httpClient.get(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_ANNOUNCEMENTS)
                    appendPathSegments(id)
                }
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }

        override suspend fun list(
            accessToken: String,
            courseId: String,
            announcementStates: List<AnnouncementState>?,
            orderBy: String?,
            pageSize: Int?,
            page: Int?,
        ): AnnouncementsDto {
            return httpClient.get(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_ANNOUNCEMENTS)
                    announcementStates?.forEach { state ->
                        parameters.append(Server.Parameters.ANNOUNCEMENT_STATES, state.name)
                    }
                    if (orderBy != null) {
                        parameters.append(Server.Parameters.ORDER_BY, orderBy)
                    }
                    if (pageSize != null) {
                        parameters.append(Server.Parameters.PAGE_SIZE, pageSize.toString())
                    }
                    if (page != null) {
                        parameters.append(Server.Parameters.PAGE, page.toString())
                    }
                }
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }

        override suspend fun patch(
            accessToken: String,
            courseId: String,
            id: String,
            updateMask: String,
            announcement: Announcement,
        ): Announcement {
            return httpClient.patch(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_ANNOUNCEMENTS)
                    appendPathSegments(id)
                    parameters.append(Server.Parameters.UPDATE_MASK, updateMask)
                }
                contentType(ContentType.Application.Json)
                setBody(announcement)
                header(HttpHeaders.Authorization, accessToken)
            }.body()
        }
    }
