package app.edumate.domain.model.announcement

import app.edumate.domain.model.material.Material
import app.edumate.domain.model.user.User

data class Announcement(
    val alternateLink: String? = null,
    val courseId: String? = null,
    val creationTime: String? = null,
    val creator: User? = null,
    val creatorUserId: String? = null,
    val id: String? = null,
    val materials: List<Material>? = null,
    val pinned: Boolean? = null,
    val text: String? = null,
    val totalComments: Int? = null,
    val updateTime: String? = null,
)
