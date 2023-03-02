package edumate.app.data.remote.dto

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import edumate.app.core.FirebaseConstants
import java.util.*

data class UsersDto(
    @ServerTimestamp
    val createdAt: Date? = null,
    val displayName: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val student: ArrayList<String>? = arrayListOf(),
    val teacher: ArrayList<String>? = arrayListOf()
) {
    @Exclude
    fun toMap(): HashMap<String, Any?> {
        return hashMapOf(
            FirebaseConstants.Firestore.CREATED_AT to FieldValue.serverTimestamp(),
            FirebaseConstants.Firestore.DISPLAY_NAME to displayName,
            FirebaseConstants.Firestore.EMAIL to email,
            FirebaseConstants.Firestore.PHOTO_URL to photoUrl,
            FirebaseConstants.Firestore.STUDENT to student,
            FirebaseConstants.Firestore.TEACHER to teacher
        )
    }
}