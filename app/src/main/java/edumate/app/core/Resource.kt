package edumate.app.core

@Deprecated("Deprecated class and will be removed later")
sealed class Resource<T>(val data: T? = null, val message: UiText? = null) {
    class Error<T>(message: UiText, data: T? = null) : Resource<T>(data, message)

    class Loading<T>(data: T? = null) : Resource<T>(data)

    class Success<T>(data: T?) : Resource<T>(data)
}
