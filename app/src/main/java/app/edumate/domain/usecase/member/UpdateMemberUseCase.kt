package app.edumate.domain.usecase.member

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.domain.model.member.UserRole
import app.edumate.domain.repository.MemberRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class UpdateMemberUseCase(
    private val memberRepository: MemberRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        courseId: String,
        userId: String,
        role: UserRole,
    ): Flow<Result<Boolean>> =
        execute(ioDispatcher) {
            memberRepository.updateMember(courseId, userId, enumValueOf(role.name))
            true
        }
}
