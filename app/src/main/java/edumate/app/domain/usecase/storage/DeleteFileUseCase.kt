package edumate.app.domain.usecase.storage

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.FirebaseStorageRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteFileUseCase @Inject constructor(
    private val storageRepository: FirebaseStorageRepository
) {
    operator fun invoke(path: String): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())
            storageRepository.deleteFile(path)
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.StringResource(Strings.error_unknown)))
        }
    }
}