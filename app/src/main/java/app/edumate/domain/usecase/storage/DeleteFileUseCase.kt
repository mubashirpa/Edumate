package app.edumate.domain.usecase.storage

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.domain.repository.StorageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class DeleteFileUseCase(
    private val storageRepository: StorageRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        bucketId: String,
        paths: List<String>,
    ): Flow<Result<Boolean>> =
        execute(ioDispatcher) {
            storageRepository.deleteFile(bucketId, paths)
            true
        }
}
