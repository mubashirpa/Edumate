package edumate.app.domain.usecase

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.model.Link
import edumate.app.domain.repository.JsoupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUrlMetadataUseCase
    @Inject
    constructor(
        private val jsoupRepository: JsoupRepository,
    ) {
        operator fun invoke(url: String): Flow<Resource<Link>> =
            flow {
                try {
                    emit(Resource.Loading())
                    val link = jsoupRepository.connect(url)
                    emit(Resource.Success(link))
                } catch (e: Exception) {
                    emit(Resource.Error(UiText.DynamicString("${e.message}")))
                }
            }
    }
