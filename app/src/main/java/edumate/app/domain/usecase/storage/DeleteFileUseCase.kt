package edumate.app.domain.usecase.storage

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.repository.FirebaseStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class DeleteFileUseCase
    @Inject
    constructor(
        private val storageRepository: FirebaseStorageRepository,
    ) {
        operator fun invoke(path: String): Flow<Result<Boolean>> =
            flow {
                try {
                    emit(Result.Loading())
                    storageRepository.deleteFile(path)
                    emit(Result.Success(true))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.error_unknown)))
                }
            }
    }
