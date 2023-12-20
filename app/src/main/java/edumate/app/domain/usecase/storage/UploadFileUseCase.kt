package edumate.app.domain.usecase.storage

import android.net.Uri
import edumate.app.core.Resource
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
        ): Flow<Resource<Uri?>> =
            flow {
                try {
                    emit(Resource.Loading())
                    val downloadUri = storageRepository.uploadFile(uri, path)
                    emit(Resource.Success(downloadUri))
                } catch (e: Exception) {
                    emit(Resource.Error(UiText.StringResource(Strings.error_unknown)))
                }
            }
    }
