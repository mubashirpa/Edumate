package edumate.app.domain.model

import java.util.*

data class User(
    val createdAt: Date? = null,
    val displayName: String? = null,
    val emailAddress: String? = null,
    val id: String = "",
    val photoUrl: String? = null,
    val enrolled: ArrayList<String> = arrayListOf(),
    val teaching: ArrayList<String> = arrayListOf(),
    val verified: Boolean = false
)