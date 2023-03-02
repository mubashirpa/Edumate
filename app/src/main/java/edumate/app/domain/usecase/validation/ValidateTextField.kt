package edumate.app.domain.usecase.validation

import edumate.app.R.string as Strings
import edumate.app.core.UiText

class ValidateTextField {
    fun execute(value: String): ValidationResult {
        if (value.isBlank()) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(Strings.error_blank_value)
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}