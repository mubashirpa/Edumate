package edumate.app.domain.usecase.validation

import edumate.app.core.UiText

data class ValidationResult(
    val successful: Boolean,
    val error: UiText? = null,
)
