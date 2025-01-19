package app.edumate.domain.usecase.validation

import app.edumate.R
import app.edumate.core.UiText

class ValidateTextField {
    fun execute(value: String): ValidationResult {
        if (value.isBlank()) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(R.string.error_blank_value),
            )
        }
        return ValidationResult(
            successful = true,
        )
    }
}
