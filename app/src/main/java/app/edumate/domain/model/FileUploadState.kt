package app.edumate.domain.model

data class FileUploadState(
    val isDone: Boolean,
    val paused: Boolean,
    val progress: Float,
    val url: String,
)
