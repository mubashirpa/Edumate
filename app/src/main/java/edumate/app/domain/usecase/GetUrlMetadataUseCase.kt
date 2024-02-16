package edumate.app.domain.usecase

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toLinkDomainModel
import edumate.app.domain.model.classroom.Link
import edumate.app.domain.repository.JsoupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUrlMetadataUseCase
    @Inject
    constructor(
        private val jsoupRepository: JsoupRepository,
    ) {
        operator fun invoke(url: String): Flow<Result<Link>> =
            flow {
                try {
                    emit(Result.Loading())
                    val link = jsoupRepository.connect(url).toLinkDomainModel()
                    emit(Result.Success(link))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.DynamicString("${e.message}")))
                }
            }
    }
