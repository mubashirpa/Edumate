package app.edumate.domain.usecase.validation

import app.edumate.R
import app.edumate.core.UiText

class ValidateRepeatedPassword {
    fun execute(
        password: String,
        repeatedPassword: String,
    ): ValidationResult {
        if (password != repeatedPassword) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(R.string.error_invalid_repeated_password),
            )
        }
        return ValidationResult(
            successful = true,
        )
    }
}
