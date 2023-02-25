package edumate.app.domain.usecase.validation

import edumate.app.R.string as Strings
import edumate.app.core.UiText

class ValidateName {
    fun execute(name: String): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(Strings.error_blank_name)
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}