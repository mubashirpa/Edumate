package app.edumate.core.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

class FileUtils(
    val context: Context,
) {
    fun getFileExtension(uri: Uri?): String? {
        val contentResolver = context.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val extension: String? =
            uri?.let { returnUri ->
                mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(returnUri))
            }
        return extension
    }

    fun getMimeType(uri: Uri?): String? {
        val contentResolver = context.contentResolver
        val mimeType: String? =
            uri?.let { returnUri ->
                contentResolver.getType(returnUri)
            }
        return mimeType
    }

    fun getMimeTypeFromUrl(url: String?): String? =
        try {
            val connection = URL(url).openConnection()
            val mimeType = connection.contentType
            connection.getInputStream().close()
            mimeType
        } catch (_: Exception) {
            null
        }

    fun getFileType(uri: Uri?): FileType {
        val mimeType = getMimeType(uri)
        return getFileTypeFromMimeType(mimeType)
    }

    fun getFileTypeFromMimeType(mimeType: String?): FileType =
        if (mimeType != null) {
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

    fun getFileTypeFromUrl(url: String?): FileType {
        val mimeType = getMimeTypeFromUrl(url)
        return getFileTypeFromMimeType(mimeType)
    }

    fun getFileName(uri: Uri?): String? {
        val contentResolver = context.contentResolver

        uri
            ?.let { returnUri ->
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

        uri
            ?.let { returnUri ->
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
                it / 1024.0.pow(digitGroups.toDouble()),
            ) + " " + units[digitGroups]
        }
        return "0"
    }

    fun uriToByteArray(uri: Uri): ByteArray {
        if (uri == Uri.EMPTY) {
            return byteArrayOf()
        }
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        if (inputStream != null) {
            return getBytes(inputStream)
        }
        return byteArrayOf()
    }

    private fun getBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }
}

enum class FileType {
    IMAGE,
    VIDEO,
    AUDIO,
    PDF,
    UNKNOWN,
}
