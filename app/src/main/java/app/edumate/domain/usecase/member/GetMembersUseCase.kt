package app.edumate.domain.usecase.member

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toUserDomainModel
import app.edumate.domain.model.user.User
import app.edumate.domain.repository.MemberRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class GetMembersUseCase(
    private val memberRepository: MemberRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(courseId: String): Flow<Result<List<User>>> =
        execute(ioDispatcher) {
            memberRepository.getMembers(courseId).map { it.toUserDomainModel() }
        }
}
