package edumate.app.domain.usecase.validation

import edumate.app.core.UiText
import edumate.app.domain.usecase.MailMatcher
import edumate.app.R.string as Strings

class ValidateEmail(
    private val mailMatcher: MailMatcher,
) {
    fun execute(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(Strings.error_blank_email),
            )
        }
        if (!mailMatcher.matches(email)) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(Strings.error_invalid_email),
            )
        }
        return ValidationResult(
            successful = true,
        )
    }
}
