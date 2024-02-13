package edumate.app.data.remote.dto.courses

import kotlinx.serialization.Serializable

@Serializable
data class CourseMaterialSet(
    val materials: List<CourseMaterial>? = null,
    val title: String? = null,
)
