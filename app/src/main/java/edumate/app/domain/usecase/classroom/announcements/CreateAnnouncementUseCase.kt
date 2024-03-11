package edumate.app.domain.usecase.classroom.announcements

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toAnnouncement
import edumate.app.data.mapper.toAnnouncementDomainModel
import edumate.app.domain.model.classroom.announcements.Announcement
import edumate.app.domain.repository.AnnouncementsRepository
import edumate.app.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class CreateAnnouncementUseCase
    @Inject
    constructor(
        private val authenticationRepository: AuthenticationRepository,
        private val announcementsRepository: AnnouncementsRepository,
    ) {
        operator fun invoke(
            courseId: String,
            announcement: Announcement,
        ): Flow<Result<Announcement>> =
            flow {
                try {
                    emit(Result.Loading())
                    val idToken = authenticationRepository.getIdToken()
                    val announcementResponse =
                        announcementsRepository.create(idToken, courseId, announcement.toAnnouncement())
                            .toAnnouncementDomainModel()
                    emit(Result.Success(announcementResponse))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.unable_to_create_announcement)))
                }
            }
    }
