package edumate.app.data.repository

import android.net.Uri
import com.google.firebase.storage.StorageReference
import edumate.app.domain.repository.FirebaseStorageRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseStorageRepositoryImpl
    @Inject
    constructor(
        private val reference: StorageReference,
    ) : FirebaseStorageRepository {
        override suspend fun uploadFile(
            uri: Uri,
            path: String,
        ): Uri? {
            val pathReference = reference.child(path)
            return pathReference.putFile(uri).await().storage.downloadUrl.await()
        }

        override suspend fun deleteFile(path: String) {
            val pathReference = reference.child(path)
            pathReference.delete().await()
        }
    }
