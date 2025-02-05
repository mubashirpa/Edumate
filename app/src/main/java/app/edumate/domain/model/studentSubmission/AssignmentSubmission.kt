package app.edumate.domain.model.studentSubmission

import app.edumate.domain.model.material.Material

data class AssignmentSubmission(
    val attachments: List<Material>? = null,
)
