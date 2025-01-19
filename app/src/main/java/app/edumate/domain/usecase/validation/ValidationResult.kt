package app.edumate.domain.usecase.validation

import app.edumate.core.UiText

data class ValidationResult(
    val successful: Boolean,
    val error: UiText? = null,
)
