package edumate.app.core.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

class FileUtils(val context: Context) {

    fun getFileExtension(uri: Uri?): String? {
        val contentResolver = context.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val extension: String? = uri?.let { returnUri ->
            mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(returnUri))
        }
        return extension
    }

    fun getMimeType(uri: Uri?): String? {
        val contentResolver = context.contentResolver
        val mimeType: String? = uri?.let { returnUri ->
            contentResolver.getType(returnUri)
        }
        return mimeType
    }

    fun getFileType(mimeType: String?): FileType {
        return if (mimeType != null) {
            when {
                mimeType.startsWith("image") -> FileType.IMAGE
                mimeType.startsWith("video") -> FileType.VIDEO
                mimeType.startsWith("audio") -> FileType.AUDIO
                mimeType.startsWith("application/pdf") -> FileType.PDF
                else -> FileType.UNKNOWN
            }
        } else {
            FileType.UNKNOWN
        }
    }

    fun getFileType(uri: Uri?): FileType {
        val mimeType = getMimeType(uri)
        return if (mimeType != null) {
            when {
                mimeType.startsWith("image") -> FileType.IMAGE
                mimeType.startsWith("video") -> FileType.VIDEO
                mimeType.startsWith("audio") -> FileType.AUDIO
                mimeType.startsWith("application/pdf") -> FileType.PDF
                else -> FileType.UNKNOWN
            }
        } else {
            FileType.UNKNOWN
        }
    }

    fun getFileName(uri: Uri?): String? {
        val contentResolver = context.contentResolver

        uri?.let { returnUri ->
            contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            return cursor.getString(nameIndex)
        }
        return null
    }

    fun getFileSize(uri: Uri?): Long? {
        val contentResolver = context.contentResolver

        uri?.let { returnUri ->
            contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            return cursor.getLong(sizeIndex)
        }
        return null
    }

    fun convertFileSizeInBytes(size: Long?): String {
        size?.let {
            if (it <= 0) return "0"
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            val digitGroups = (log10(it.toDouble()) / log10(1024.0)).toInt()
            return DecimalFormat("#,##0.#").format(
                it / 1024.0.pow(digitGroups.toDouble())
            ) + " " + units[digitGroups]
        }
        return "0"
    }
}

enum class FileType {
    IMAGE,
    VIDEO,
    AUDIO,
    PDF,
    UNKNOWN
}