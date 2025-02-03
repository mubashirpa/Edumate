package app.edumate.domain.usecase.announcement

import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.data.mapper.toComment
import app.edumate.domain.model.comment.Comment
import app.edumate.domain.repository.AnnouncementRepository
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetAnnouncementCommentsUseCase(
    private val announcementRepository: AnnouncementRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(id: String): Flow<Result<List<Comment>>> =
        flow {
            try {
                emit(Result.Loading())
                val comments = announcementRepository.getComments(id).map { it.toComment() }
                emit(Result.Success(comments))
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
