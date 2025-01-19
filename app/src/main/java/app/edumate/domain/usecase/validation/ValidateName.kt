package app.edumate.domain.usecase.validation

import app.edumate.R
import app.edumate.core.UiText

class ValidateName {
    fun execute(name: String): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(R.string.error_blank_name),
            )
        }
        return ValidationResult(
            successful = true,
        )
    }
}
