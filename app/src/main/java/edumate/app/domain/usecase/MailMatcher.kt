package edumate.app.domain.usecase

interface MailMatcher {
    fun matches(mail: String): Boolean
}