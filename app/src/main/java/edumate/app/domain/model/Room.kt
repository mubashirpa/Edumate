package edumate.app.domain.model

import java.util.*
import kotlin.collections.ArrayList

data class Room(
    val creationDate: Date? = null,
    val description: String? = null,
    val id: String? = null,
    val link: String? = null,
    val members: ArrayList<String>? = arrayListOf(),
    val section: String? = null,
    val subject: String? = null,
    val title: String? = null
)