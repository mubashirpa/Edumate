package edumate.app.data.remote.dto

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import edumate.app.core.FirebaseConstants
import java.util.*

@IgnoreExtraProperties
data class UserProfileDto(
    @ServerTimestamp
    val createdAt: Date? = null,
    val displayName: String? = null,
    val emailAddress: String? = null,
    val id: String = "",
    val photoUrl: String? = null,
    val enrolled: ArrayList<String> = arrayListOf(),
    val teaching: ArrayList<String> = arrayListOf(),
    val verified: Boolean = false,
) {
    @Exclude
    fun toMap(): HashMap<String, Any?> {
        return hashMapOf(
            FirebaseConstants.Firestore.CREATED_AT to (createdAt ?: FieldValue.serverTimestamp()),
            FirebaseConstants.Firestore.DISPLAY_NAME to displayName,
            FirebaseConstants.Firestore.EMAIL_ADDRESS to emailAddress,
            FirebaseConstants.Firestore.ID to id,
            FirebaseConstants.Firestore.PHOTO_URL to photoUrl,
            FirebaseConstants.Firestore.ENROLLED to enrolled,
            FirebaseConstants.Firestore.TEACHING to teaching,
            FirebaseConstants.Firestore.VERIFIED to verified,
        )
    }
}
