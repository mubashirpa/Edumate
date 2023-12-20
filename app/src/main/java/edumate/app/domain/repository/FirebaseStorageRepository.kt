package edumate.app.domain.repository

import android.net.Uri
import com.google.firebase.storage.StorageReference

interface FirebaseStorageRepository {
    /**
     * Asynchronously uploads from a content URI to this [StorageReference].
     * @param uri The source of the upload. This can be a file:// scheme or any content URI. A content resolver will be used to load the data.
     */
    suspend fun uploadFile(
        uri: Uri,
        path: String,
    ): Uri?

    /**
     * Deletes the object at this [StorageReference].
     */
    suspend fun deleteFile(path: String)
}
