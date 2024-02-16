package edumate.app.domain.repository

import android.net.Uri

interface FirebaseStorageRepository {
    /**
     * Uploads a file from a content [Uri].
     * @param uri The source of the upload. This can be a file:// scheme or any content [Uri].
     * @param path The destination path in the storage where the file will be uploaded.
     * @return A [Uri] representing the uploaded file, or null if the upload fails.
     */
    suspend fun uploadFile(
        uri: Uri,
        path: String,
    ): Uri?

    /**
     * Deletes a file from the storage at the specified path.
     * @param path The path of the file to delete.
     */
    suspend fun deleteFile(path: String)
}
