package app.edumate.core.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build

object ClipboardUtils {
    fun copyTextToClipboard(
        context: Context,
        textCopied: String,
        onSuccess: () -> Unit,
    ) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText(textCopied, textCopied))
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            onSuccess()
        }
    }
}
