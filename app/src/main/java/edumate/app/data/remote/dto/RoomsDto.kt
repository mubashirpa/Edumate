package edumate.app.data.remote.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

data class RoomsDto(
    @DocumentId
    val roomId: String? = null,
    @ServerTimestamp
    val creationDate: Date? = null,
    val description: String? = null,
    val id: String? = null,
    val link: String? = null,
    val members: ArrayList<String>? = arrayListOf(),
    val section: String? = null,
    val subject: String? = null,
    val title: String? = null
) {
    @Exclude
    fun toMap(): HashMap<String, Any?> {
        return hashMapOf(
            "creationDate" to FieldValue.serverTimestamp() /* Or Timestamp(Date())*/,
            "description" to description,
            "id" to id,
            "link" to link,
            "members" to members,
            "section" to section,
            "subject" to subject,
            "title" to title
        )
    }
}