package app.edumate.di

import app.edumate.data.AndroidMailMatcher
import app.edumate.domain.MailMatcher
import app.edumate.domain.usecase.GetUrlMetadataUseCase
import app.edumate.domain.usecase.announcement.CreateAnnouncementUseCase
import app.edumate.domain.usecase.announcement.DeleteAnnouncementUseCase
import app.edumate.domain.usecase.announcement.GetAnnouncementsUseCase
import app.edumate.domain.usecase.announcement.UpdateAnnouncementUseCase
import app.edumate.domain.usecase.authentication.GetCurrentUserUseCase
import app.edumate.domain.usecase.authentication.GetSignInInfoUseCase
import app.edumate.domain.usecase.authentication.IsUserLoggedInUseCase
import app.edumate.domain.usecase.authentication.ResendSignUpConfirmationEmailUseCase
import app.edumate.domain.usecase.authentication.ResetPasswordUseCase
import app.edumate.domain.usecase.authentication.SignInUseCase
import app.edumate.domain.usecase.authentication.SignInWithGoogleUseCase
import app.edumate.domain.usecase.authentication.SignOutUseCase
import app.edumate.domain.usecase.authentication.SignUpUseCase
import app.edumate.domain.usecase.authentication.UpdatePasswordUseCase
import app.edumate.domain.usecase.course.CreateCourseUseCase
import app.edumate.domain.usecase.course.DeleteCourseUseCase
import app.edumate.domain.usecase.course.GetCourseUseCase
import app.edumate.domain.usecase.course.GetCourseWithMembersUseCase
import app.edumate.domain.usecase.course.GetCoursesUseCase
import app.edumate.domain.usecase.course.UpdateCourseUseCase
import app.edumate.domain.usecase.courseWork.CreateAssignmentUseCase
import app.edumate.domain.usecase.courseWork.CreateMaterialUseCase
import app.edumate.domain.usecase.courseWork.CreateQuestionUseCase
import app.edumate.domain.usecase.courseWork.DeleteCourseWorkUseCase
import app.edumate.domain.usecase.courseWork.GetCourseWorkUseCase
import app.edumate.domain.usecase.courseWork.GetCourseWorksUseCase
import app.edumate.domain.usecase.courseWork.UpdateCourseWorkUseCase
import app.edumate.domain.usecase.member.DeleteMemberUseCase
import app.edumate.domain.usecase.member.GetMembersUseCase
import app.edumate.domain.usecase.member.JoinCourseUseCase
import app.edumate.domain.usecase.member.UnenrollCourseUseCase
import app.edumate.domain.usecase.storage.DeleteFileUseCase
import app.edumate.domain.usecase.storage.UploadFileUseCase
import app.edumate.domain.usecase.validation.ValidateEmail
import app.edumate.domain.usecase.validation.ValidateName
import app.edumate.domain.usecase.validation.ValidatePassword
import app.edumate.domain.usecase.validation.ValidateRepeatedPassword
import app.edumate.domain.usecase.validation.ValidateTextField
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule =
    module {
        singleOf(::CreateAnnouncementUseCase)
        singleOf(::CreateCourseUseCase)
        singleOf(::CreateAssignmentUseCase)
        singleOf(::CreateMaterialUseCase)
        singleOf(::CreateQuestionUseCase)
        singleOf(::DeleteAnnouncementUseCase)
        singleOf(::DeleteCourseUseCase)
        singleOf(::DeleteCourseWorkUseCase)
        singleOf(::DeleteFileUseCase)
        singleOf(::DeleteMemberUseCase)
        singleOf(::GetAnnouncementsUseCase)
        singleOf(::GetCourseUseCase)
        singleOf(::GetCourseWithMembersUseCase)
        singleOf(::GetCourseWorkUseCase)
        singleOf(::GetCourseWorksUseCase)
        singleOf(::GetCoursesUseCase)
        singleOf(::GetCurrentUserUseCase)
        singleOf(::GetMembersUseCase)
        singleOf(::GetSignInInfoUseCase)
        singleOf(::GetUrlMetadataUseCase)
        singleOf(::IsUserLoggedInUseCase)
        singleOf(::JoinCourseUseCase)
        singleOf(::ResendSignUpConfirmationEmailUseCase)
        singleOf(::ResetPasswordUseCase)
        singleOf(::SignInUseCase)
        singleOf(::SignInWithGoogleUseCase)
        singleOf(::SignOutUseCase)
        singleOf(::SignUpUseCase)
        singleOf(::UnenrollCourseUseCase)
        singleOf(::UpdateAnnouncementUseCase)
        singleOf(::UpdateCourseUseCase)
        singleOf(::UpdateCourseWorkUseCase)
        singleOf(::UpdatePasswordUseCase)
        singleOf(::UploadFileUseCase)
        singleOf(::ValidateEmail)
        singleOf(::ValidateName)
        singleOf(::ValidatePassword)
        singleOf(::ValidateRepeatedPassword)
        singleOf(::ValidateTextField)
        single<MailMatcher> { AndroidMailMatcher() }
        single { Dispatchers.IO }
    }
