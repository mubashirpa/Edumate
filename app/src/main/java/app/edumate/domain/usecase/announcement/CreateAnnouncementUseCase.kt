package app.edumate.domain.usecase.announcement

import app.edumate.R
import app.edumate.core.Constants
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.data.mapper.toAnnouncementDomainModel
import app.edumate.data.mapper.toAnnouncementDto
import app.edumate.domain.model.announcement.Announcement
import app.edumate.domain.model.material.Material
import app.edumate.domain.repository.AnnouncementRepository
import app.edumate.domain.repository.AuthenticationRepository
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.UUID

class CreateAnnouncementUseCase(
    private val announcementRepository: AnnouncementRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        courseId: String,
        text: String,
        materials: List<Material>?,
        id: String = UUID.randomUUID().toString(),
    ): Flow<Result<Announcement>> =
        flow {
            try {
                emit(Result.Loading())
                authenticationRepository.currentUser()?.id?.let { userId ->
                    val announcement =
                        Announcement(
                            alternateLink = "${Constants.EDUMATE_BASE_URL}c/$courseId/p/$id/details",
                            courseId = courseId,
                            creatorUserId = userId,
                            id = id,
                            materials = materials.takeIf { !it.isNullOrEmpty() },
                            text = text,
                        ).toAnnouncementDto()
                    val result =
                        announcementRepository
                            .createAnnouncement(announcement)
                            .toAnnouncementDomainModel()
                    emit(Result.Success(result))
                } ?: emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            } catch (_: RestException) {
                emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            } catch (_: HttpRequestTimeoutException) {
                emit(Result.Error(UiText.StringResource(R.string.error_timeout_exception)))
            } catch (_: HttpRequestException) {
                emit(Result.Error(UiText.StringResource(R.string.error_network_exception)))
            } catch (_: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unknown)))
            }
        }.flowOn(ioDispatcher)
}
