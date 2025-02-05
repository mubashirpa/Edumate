package app.edumate.data.remote.dto.studentSubmission

import app.edumate.data.remote.dto.material.MaterialDto
import kotlinx.serialization.Serializable

@Serializable
data class AssignmentSubmissionDto(
    val attachments: List<MaterialDto>? = null,
)
