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
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import javax.inject.Inject

class AnnouncementsRepositoryImpl
    @Inject
    constructor(
        private val httpClient: HttpClient,
    ) : AnnouncementsRepository {
        override suspend fun create(
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
            }.body()
        }

        override suspend fun delete(
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
            }
        }

        override suspend fun get(
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
            }.body()
        }

        override suspend fun list(
            courseId: String,
            announcementStates: List<AnnouncementState>?,
            orderBy: String?,
            pageSize: Int?,
            pageToken: String?,
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
                    if (pageToken != null) {
                        parameters.append(Server.Parameters.PAGE_TOKEN, pageToken)
                    }
                }
            }.body()
        }

        override suspend fun update(
            courseId: String,
            id: String,
            announcement: Announcement,
        ): Announcement {
            return httpClient.put(Server.API_BASE_URL) {
                url {
                    appendPathSegments(Server.ENDPOINT_COURSES)
                    appendPathSegments(courseId)
                    appendPathSegments(Server.ENDPOINT_ANNOUNCEMENTS)
                    appendPathSegments(id)
                }
                contentType(ContentType.Application.Json)
                setBody(announcement)
            }.body()
        }
    }
