package edumate.app.domain.usecase.storage

import android.net.Uri
import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.repository.FirebaseStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class UploadFileUseCase
    @Inject
    constructor(
        private val storageRepository: FirebaseStorageRepository,
    ) {
        operator fun invoke(
            uri: Uri,
            path: String,
        ): Flow<Result<Uri?>> =
            flow {
                try {
                    emit(Result.Loading())
                    val downloadUri = storageRepository.uploadFile(uri, path)
                    emit(Result.Success(downloadUri))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.error_unknown)))
                }
            }
    }
