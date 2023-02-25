package edumate.app.domain.usecase.validation

import edumate.app.R.string as Strings
import edumate.app.core.UiText

class ValidateRepeatedPassword {
    fun execute(password: String, repeatedPassword: String): ValidationResult {
        if (password != repeatedPassword) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(Strings.error_invalid_repeated_password)
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}