package edumate.app.domain.repository

import android.net.Uri

interface DynamicLinksRepository {
    suspend fun create(url: String): Uri?
}