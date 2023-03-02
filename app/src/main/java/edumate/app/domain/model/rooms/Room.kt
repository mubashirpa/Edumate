package edumate.app.domain.model.rooms

import java.util.*
import kotlin.collections.ArrayList

data class Room(
    val createdBy: CreatedBy? = null,
    val creationDate: Date? = null,
    val description: String? = null,
    val id: String? = null,
    val link: String? = null,
    val section: String? = null,
    val students: ArrayList<String>? = arrayListOf(),
    val subject: String? = null,
    val teachers: ArrayList<String>? = arrayListOf(),
    val title: String? = null
)