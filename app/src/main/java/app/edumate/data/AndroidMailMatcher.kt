package app.edumate.data

import android.util.Patterns
import app.edumate.domain.MailMatcher

class AndroidMailMatcher : MailMatcher {
    override fun matches(mail: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(mail).matches()
}
