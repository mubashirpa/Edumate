package edumate.app.data.remote.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import edumate.app.core.FirebaseConstants
import edumate.app.domain.model.rooms.CreatedBy
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

data class RoomsDto(
    @DocumentId
    val roomId: String? = null,
    val createdBy: CreatedBy? = null,
    @ServerTimestamp
    val creationDate: Date? = null,
    val description: String? = null,
    val id: String? = null,
    val link: String? = null,
    val section: String? = null,
    val students: ArrayList<String>? = arrayListOf(),
    val subject: String? = null,
    val teachers: ArrayList<String>? = arrayListOf(),
    val title: String? = null
) {
    @Exclude
    fun toMap(): HashMap<String, Any?> {
        return hashMapOf(
            FirebaseConstants.Firestore.CREATED_BY to createdBy,
            FirebaseConstants.Firestore.CREATION_DATE to FieldValue.serverTimestamp() /* (OR) Timestamp(Date()) */,
            FirebaseConstants.Firestore.DESCRIPTION to description,
            FirebaseConstants.Firestore.ID to id,
            FirebaseConstants.Firestore.LINK to link,
            FirebaseConstants.Firestore.SECTION to section,
            FirebaseConstants.Firestore.STUDENTS to students,
            FirebaseConstants.Firestore.SUBJECT to subject,
            FirebaseConstants.Firestore.TEACHERS to teachers,
            FirebaseConstants.Firestore.TITLE to title
        )
    }
}