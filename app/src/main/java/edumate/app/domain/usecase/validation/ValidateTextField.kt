package edumate.app.domain.usecase.validation

import edumate.app.core.UiText
import edumate.app.R.string as Strings

class ValidateTextField {
    fun execute(value: String): ValidationResult {
        if (value.isBlank()) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(Strings.error_blank_value),
            )
        }
        return ValidationResult(
            successful = true,
        )
    }
}
