package app.edumate.domain.usecase.member

import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.repository.AuthenticationRepository
import app.edumate.domain.repository.MemberRepository
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class JoinCourseUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val memberRepository: MemberRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(courseId: String): Flow<Result<Boolean>> =
        flow {
            try {
                emit(Result.Loading())
                authenticationRepository.currentUser()?.id?.let { userId ->
                    memberRepository.insertMember(courseId, userId)
                    emit(Result.Success(true))
                }
                    ?: emit(Result.Error(UiText.StringResource(R.string.error_unauthenticated_access_exception)))
            } catch (e: RestException) {
                emit(Result.Error(e.toUiText()))
            } catch (_: HttpRequestTimeoutException) {
                emit(Result.Error(UiText.StringResource(R.string.error_timeout_exception)))
            } catch (_: HttpRequestException) {
                emit(Result.Error(UiText.StringResource(R.string.error_network_exception)))
            } catch (_: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unknown)))
            }
        }.flowOn(ioDispatcher)

    private fun RestException.toUiText(): UiText =
        when (statusCode) {
            HttpStatusCode.BadRequest.value -> {
                UiText.StringResource(R.string.error_join_course_invalid_id)
            }

            HttpStatusCode.Conflict.value -> {
                when {
                    error.contains("members_pkey", ignoreCase = true) -> {
                        UiText.StringResource(R.string.error_join_course_already_joined)
                    }

                    error.contains("members_course_id_fkey", ignoreCase = true) -> {
                        UiText.StringResource(R.string.error_join_course_not_found)
                    }

                    else -> {
                        UiText.StringResource(R.string.error_rest_exception)
                    }
                }
            }

            else -> {
                UiText.StringResource(R.string.error_rest_exception)
            }
        }
}
