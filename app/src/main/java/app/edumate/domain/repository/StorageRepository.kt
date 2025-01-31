package app.edumate.domain.repository

import app.edumate.domain.model.FileUploadState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StorageRepository {
    suspend fun uploadFile(
        bucketId: String,
        path: String,
        file: File,
    ): Flow<FileUploadState>

    suspend fun deleteFile(
        bucketId: String,
        paths: List<String>,
    )
}
