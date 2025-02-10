package app.edumate.domain.usecase.member

import app.edumate.core.Result
import app.edumate.core.UnauthenticatedAccessException
import app.edumate.core.utils.execute
import app.edumate.domain.repository.AuthenticationRepository
import app.edumate.domain.repository.MemberRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class UnenrollCourseUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val memberRepository: MemberRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(courseId: String): Flow<Result<Boolean>> =
        execute(ioDispatcher) {
            val userId =
                authenticationRepository.currentUser()?.id ?: throw UnauthenticatedAccessException()
            memberRepository.deleteMember(courseId, userId)
            true
        }
}
