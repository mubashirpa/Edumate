package app.edumate.data.repository

import app.edumate.BuildConfig
import app.edumate.domain.model.FileUploadState
import app.edumate.domain.repository.StorageRepository
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.createOrContinueUpload
import io.github.jan.supabase.storage.resumable.ResumableUpload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.io.File

class StorageRepositoryImpl(
    private val storage: Storage,
) : StorageRepository {
    override suspend fun uploadFile(
        bucketId: String,
        path: String,
        file: File,
    ): Flow<FileUploadState> {
        val upload: ResumableUpload =
            storage.from(bucketId.bucket()).resumable.createOrContinueUpload(path, file)

        return upload.stateFlow
            .map { state ->
                FileUploadState(
                    isDone = state.isDone,
                    paused = state.paused,
                    progress = state.progress,
                    url = buildImageUrl(state.bucketId, state.path),
                )
            }.onStart {
                upload.startOrResumeUploading()
            }
    }

    override suspend fun deleteFile(
        bucketId: String,
        paths: List<String>,
    ) {
        storage.from(bucketId.bucket()).delete(paths)
    }

    private fun buildImageUrl(
        bucketId: String,
        path: String,
    ) = "${BuildConfig.SUPABASE_URL}/storage/v1/object/public/$bucketId/$path"

    private fun String.bucket(): String = replace(" ", "%20")
}
