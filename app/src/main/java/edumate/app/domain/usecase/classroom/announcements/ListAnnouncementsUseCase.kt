package edumate.app.domain.usecase.classroom.announcements

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toAnnouncementDomainModel
import edumate.app.domain.model.classroom.announcements.Announcement
import edumate.app.domain.model.classroom.announcements.AnnouncementState
import edumate.app.domain.repository.AnnouncementsRepository
import edumate.app.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class ListAnnouncementsUseCase
    @Inject
    constructor(
        private val authenticationRepository: AuthenticationRepository,
        private val announcementsRepository: AnnouncementsRepository,
    ) {
        operator fun invoke(
            courseId: String,
            announcementStates: List<AnnouncementState>? = listOf(AnnouncementState.PUBLISHED),
            orderBy: String? = "updateTime desc",
            pageSize: Int? = null,
            page: Int? = null,
        ): Flow<Result<List<Announcement>>> =
            flow {
                try {
                    emit(Result.Loading())
                    val idToken = authenticationRepository.getIdToken()
                    val announcements =
                        announcementsRepository.list(
                            idToken,
                            courseId,
                            announcementStates?.map { enumValueOf(it.name) },
                            orderBy,
                            pageSize,
                            page,
                        ).announcements?.map { it.toAnnouncementDomainModel() }
                    emit(Result.Success(announcements))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.cannot_retrieve_announcements_at_this_time_please_try_again_later)))
                }
            }
    }
