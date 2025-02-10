package app.edumate.domain.usecase.member

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.domain.repository.MemberRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class DeleteMemberUseCase(
    private val memberRepository: MemberRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        courseId: String,
        userId: String,
    ): Flow<Result<Boolean>> =
        execute(ioDispatcher) {
            memberRepository.deleteMember(courseId, userId)
            true
        }
}
