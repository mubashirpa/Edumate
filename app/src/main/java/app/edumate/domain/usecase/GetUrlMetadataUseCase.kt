package app.edumate.domain.usecase

import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.data.mapper.toLinkDomainModel
import app.edumate.domain.model.material.Link
import app.edumate.domain.repository.JsoupRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetUrlMetadataUseCase(
    private val jsoupRepository: JsoupRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(url: String): Flow<Result<Link>> =
        flow {
            try {
                emit(Result.Loading())
                val link = jsoupRepository.getUrlMetadata(url).toLinkDomainModel()
                emit(Result.Success(link))
            } catch (_: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            }
        }.flowOn(ioDispatcher)
}
