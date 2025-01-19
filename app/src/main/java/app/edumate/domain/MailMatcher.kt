package app.edumate.domain

interface MailMatcher {
    fun matches(mail: String): Boolean
}
