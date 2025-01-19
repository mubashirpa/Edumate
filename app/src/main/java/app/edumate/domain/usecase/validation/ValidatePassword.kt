package app.edumate.domain.usecase.validation

import app.edumate.R
import app.edumate.core.UiText

class ValidatePassword {
    fun execute(password: String): ValidationResult {
        if (password.length < 6) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(R.string.error_blank_password),
            )
        }
        val containsLettersAndDigits =
            password.any { it.isDigit() } && password.any { it.isLetter() }
        if (!containsLettersAndDigits) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(R.string.error_invalid_password),
            )
        }
        return ValidationResult(
            successful = true,
        )
    }
}
