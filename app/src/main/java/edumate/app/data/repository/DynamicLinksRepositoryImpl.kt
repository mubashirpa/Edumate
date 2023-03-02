package edumate.app.data.repository

import android.net.Uri
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import edumate.app.BuildConfig
import edumate.app.core.FirebaseConstants
import edumate.app.domain.repository.DynamicLinksRepository
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class DynamicLinksRepositoryImpl @Inject constructor(
    private val dynamicLinks: FirebaseDynamicLinks
) : DynamicLinksRepository {

    override suspend fun create(url: String): Uri? {
        return dynamicLinks.shortLinkAsync {
            link = Uri.parse(url)
            domainUriPrefix = FirebaseConstants.DynamicLinks.DOMAIN_URI_PREFIX

            androidParameters(BuildConfig.APPLICATION_ID) {
                fallbackUrl = Uri.parse(url)
            }
        }.await().shortLink
    }
}