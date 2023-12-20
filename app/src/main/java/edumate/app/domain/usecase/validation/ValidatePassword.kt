package edumate.app.domain.usecase.validation

import edumate.app.core.UiText
import edumate.app.R.string as Strings

class ValidatePassword {
    fun execute(password: String): ValidationResult {
        if (password.length < 6) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(Strings.error_blank_password),
            )
        }
        val containsLettersAndDigits =
            password.any { it.isDigit() } && password.any { it.isLetter() }
        if (!containsLettersAndDigits) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(Strings.error_invalid_password),
            )
        }
        return ValidationResult(
            successful = true,
        )
    }
}
