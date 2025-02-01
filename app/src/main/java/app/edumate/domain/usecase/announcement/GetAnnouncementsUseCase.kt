package app.edumate.domain.usecase.announcement

import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.data.mapper.toAnnouncementDomainModel
import app.edumate.domain.model.announcement.Announcement
import app.edumate.domain.repository.AnnouncementRepository
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetAnnouncementsUseCase(
    private val announcementRepository: AnnouncementRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(courseId: String): Flow<Result<List<Announcement>>> =
        flow {
            try {
                emit(Result.Loading())
                val announcements =
                    announcementRepository.getAnnouncements(courseId).map {
                        it.toAnnouncementDomainModel()
                    }
                emit(Result.Success(announcements))
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
