package app.edumate.domain.model.comment

import app.edumate.domain.model.user.User

data class Comment(
    val creationTime: String? = null,
    val creator: User? = null,
    val creatorUserId: String? = null,
    val id: String? = null,
    val text: String? = null,
    val updateTime: String? = null,
)
