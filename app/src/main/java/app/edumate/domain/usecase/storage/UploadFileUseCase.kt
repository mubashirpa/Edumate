package app.edumate.domain.usecase.storage

import app.edumate.domain.model.FileUploadState
import app.edumate.domain.repository.StorageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

class UploadFileUseCase(
    private val storageRepository: StorageRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(
        bucketName: String,
        filePath: String,
        file: File,
    ): Flow<FileUploadState> = storageRepository.uploadFile(bucketName, filePath, file).flowOn(ioDispatcher)
}
