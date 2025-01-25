package app.edumate.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

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

    fun composeEmail(
        context: Context,
        addresses: Array<String>,
    ) {
        val intent =
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // only email apps should handle this
                putExtra(Intent.EXTRA_EMAIL, addresses)
            }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
}
