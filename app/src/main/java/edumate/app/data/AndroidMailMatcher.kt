package edumate.app.data

import android.util.Patterns
import edumate.app.domain.usecase.MailMatcher

class AndroidMailMatcher : MailMatcher {
    override fun matches(mail: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(mail).matches()
    }
}
