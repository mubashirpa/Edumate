package edumate.app.domain.usecase.storage

import android.net.Uri
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.FirebaseStorageRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UploadFileUseCase @Inject constructor(
    private val repository: FirebaseStorageRepository
) {
    operator fun invoke(uri: Uri, path: String): Flow<Resource<Uri?>> = flow {
        try {
            emit(Resource.Loading())
            val downloadUri = repository.uploadFile(uri, path)
            emit(Resource.Success(downloadUri))
        } catch (e: Exception) {
            // TODO("Add appropriate error messages")
            emit(Resource.Error(UiText.DynamicString(e.message.toString())))
        }
    }
}