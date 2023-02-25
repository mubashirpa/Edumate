package edumate.app.core

sealed class Resource<T>(
    val data: T? = null,
    val message: UiText? = null,
    val progress: Int? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: UiText, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(progress: Int? = null, data: T? = null) :
        Resource<T>(data, progress = progress)
}