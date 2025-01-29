package app.edumate.core.utils

import android.os.Build
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object CryptographyUtils {
    fun generateShortHash(
        input: String,
        secretKey: String,
        length: Int = 6,
    ): String {
        val mac = Mac.getInstance("HmacSHA256")
        val keySpec = SecretKeySpec(secretKey.toByteArray(), "HmacSHA256")
        mac.init(keySpec)

        val hash = mac.doFinal(input.toByteArray())
        // Take only first `length` bytes for better entropy before encoding
        val shortenedHash = hash.sliceArray(0 until (length.coerceAtMost(hash.size)))

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getUrlEncoder().withoutPadding().encodeToString(shortenedHash)
        } else {
            bytesToHex(shortenedHash)
        }
    }

    private fun bytesToHex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }
}
