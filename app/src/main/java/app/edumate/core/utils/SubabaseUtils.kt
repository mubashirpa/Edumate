package app.edumate.core.utils

import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.core.UnauthenticatedAccessException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * Extension function to convert a Throwable to a user-friendly UiText.
 *
 * This function provides a centralized way to handle different types of exceptions
 * and convert them into messages that can be displayed to the user.
 */
fun Throwable.toUiText(): UiText =
    when (this) {
        is RestException -> UiText.StringResource(R.string.error_rest_exception)
        is HttpRequestTimeoutException -> UiText.StringResource(R.string.error_timeout_exception)
        is HttpRequestException -> UiText.StringResource(R.string.error_network_exception)
        is UnauthenticatedAccessException -> UiText.StringResource(R.string.error_unauthenticated_access_exception)
        else -> UiText.StringResource(R.string.error_unknown)
    }

/**
 * Executes a suspending block of code within a Flow, handling loading, success, and error states.
 *
 * This function simplifies the process of executing network requests or other asynchronous
 * operations and managing their results in a reactive way.
 *
 * @param context The CoroutineDispatcher on which the block should be executed.
 * @param block The suspending block of code to execute.
 * @return A Flow emitting Result states: Loading, Success, or Error.
 */
fun <T> execute(
    context: CoroutineDispatcher,
    block: suspend () -> T,
): Flow<Result<T>> =
    flow<Result<T>> {
        emit(Result.Loading())
        val result = withContext(context) { block() }
        emit(Result.Success(result))
    }.catch { throwable ->
        emit(Result.Error(throwable.toUiText()))
    }
