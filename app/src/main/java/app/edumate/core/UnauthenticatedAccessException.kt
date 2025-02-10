package app.edumate.core

class UnauthenticatedAccessException(
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause)
