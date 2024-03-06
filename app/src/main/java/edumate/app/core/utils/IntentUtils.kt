package edumate.app.core.utils

import android.content.Context
import android.content.Intent

object IntentUtils {
    fun shareText(
        context: Context,
        text: String,
    ) {
        val sendIntent: Intent =
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }
}
