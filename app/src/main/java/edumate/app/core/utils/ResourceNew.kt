package edumate.app.core.utils

import edumate.app.core.UiText

sealed class ResourceNew<T>(val data: T? = null, val message: UiText? = null) {
    class Error<T>(message: UiText, data: T? = null) : ResourceNew<T>(data, message)

    class Loading<T>(data: T? = null) : ResourceNew<T>(data)

    class Success<T>(data: T?) : ResourceNew<T>(data)

    class Unknown<T> : ResourceNew<T>()
}
