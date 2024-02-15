package edumate.app.data.remote.dto.classroom

import kotlinx.serialization.Serializable

@Serializable
data class IndividualStudentsOptions(
    val studentIds: List<String>? = null,
)
