package app.edumate.domain.usecase.validation

import app.edumate.R
import app.edumate.core.UiText
import app.edumate.domain.MailMatcher

class ValidateEmail(
    private val mailMatcher: MailMatcher,
) {
    fun execute(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(R.string.error_blank_email),
            )
        }
        if (!mailMatcher.matches(email)) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(R.string.error_invalid_email),
            )
        }
        return ValidationResult(
            successful = true,
        )
    }
}
