package app.edumate.di

import app.edumate.domain.usecase.GetUrlMetadataUseCase
import app.edumate.domain.usecase.announcement.CreateAnnouncementCommentUseCase
import app.edumate.domain.usecase.announcement.CreateAnnouncementUseCase
import app.edumate.domain.usecase.announcement.DeleteAnnouncementUseCase
import app.edumate.domain.usecase.announcement.GetAnnouncementCommentsUseCase
import app.edumate.domain.usecase.announcement.GetAnnouncementsUseCase
import app.edumate.domain.usecase.announcement.UpdateAnnouncementUseCase
import app.edumate.domain.usecase.authentication.GetCurrentUserUseCase
import app.edumate.domain.usecase.authentication.GetLoginPreferencesUseCase
import app.edumate.domain.usecase.authentication.IsUserLoggedInUseCase
import app.edumate.domain.usecase.authentication.ResendSignUpConfirmationEmailUseCase
import app.edumate.domain.usecase.authentication.ResetPasswordUseCase
import app.edumate.domain.usecase.authentication.SignInUseCase
import app.edumate.domain.usecase.authentication.SignInWithGoogleUseCase
import app.edumate.domain.usecase.authentication.SignOutUseCase
import app.edumate.domain.usecase.authentication.SignUpUseCase
import app.edumate.domain.usecase.authentication.UpdatePasswordUseCase
import app.edumate.domain.usecase.comment.DeleteCommentUseCase
import app.edumate.domain.usecase.comment.UpdateCommentUseCase
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
import app.edumate.domain.usecase.member.UpdateMemberUseCase
import app.edumate.domain.usecase.preferences.ConfigureAppThemeUseCase
import app.edumate.domain.usecase.preferences.GetUserPreferencesUseCase
import app.edumate.domain.usecase.preferences.UpdateReviewDialogShownUseCase
import app.edumate.domain.usecase.storage.DeleteFileUseCase
import app.edumate.domain.usecase.storage.UploadFileUseCase
import app.edumate.domain.usecase.studentSubmission.CreateSubmissionCommentUseCase
import app.edumate.domain.usecase.studentSubmission.GetStudentSubmissionUseCase
import app.edumate.domain.usecase.studentSubmission.GetStudentSubmissionsUseCase
import app.edumate.domain.usecase.studentSubmission.GetSubmissionCommentsUseCase
import app.edumate.domain.usecase.studentSubmission.ModifyStudentSubmissionAttachmentsUseCase
import app.edumate.domain.usecase.studentSubmission.ReclaimStudentSubmissionUseCase
import app.edumate.domain.usecase.studentSubmission.ReturnStudentSubmissionUseCase
import app.edumate.domain.usecase.studentSubmission.TurnInStudentSubmissionUseCase
import app.edumate.domain.usecase.studentSubmission.UpdateStudentSubmissionUseCase
import app.edumate.domain.usecase.validation.ValidateEmail
import app.edumate.domain.usecase.validation.ValidateName
import app.edumate.domain.usecase.validation.ValidatePassword
import app.edumate.domain.usecase.validation.ValidateRepeatedPassword
import app.edumate.domain.usecase.validation.ValidateTextField
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule =
    module {
        singleOf(::ConfigureAppThemeUseCase)
        singleOf(::CreateAnnouncementCommentUseCase)
        singleOf(::CreateAnnouncementUseCase)
        singleOf(::CreateAssignmentUseCase)
        singleOf(::CreateCourseUseCase)
        singleOf(::CreateMaterialUseCase)
        singleOf(::CreateQuestionUseCase)
        singleOf(::CreateSubmissionCommentUseCase)
        singleOf(::DeleteAnnouncementUseCase)
        singleOf(::DeleteCommentUseCase)
        singleOf(::DeleteCourseUseCase)
        singleOf(::DeleteCourseWorkUseCase)
        singleOf(::DeleteFileUseCase)
        singleOf(::DeleteMemberUseCase)
        singleOf(::GetAnnouncementCommentsUseCase)
        singleOf(::GetAnnouncementsUseCase)
        singleOf(::GetCourseUseCase)
        singleOf(::GetCourseWithMembersUseCase)
        singleOf(::GetCourseWorkUseCase)
        singleOf(::GetCourseWorksUseCase)
        singleOf(::GetCoursesUseCase)
        singleOf(::GetCurrentUserUseCase)
        singleOf(::GetLoginPreferencesUseCase)
        singleOf(::GetMembersUseCase)
        singleOf(::GetStudentSubmissionUseCase)
        singleOf(::GetStudentSubmissionsUseCase)
        singleOf(::GetSubmissionCommentsUseCase)
        singleOf(::GetUrlMetadataUseCase)
        singleOf(::GetUserPreferencesUseCase)
        singleOf(::IsUserLoggedInUseCase)
        singleOf(::JoinCourseUseCase)
        singleOf(::ModifyStudentSubmissionAttachmentsUseCase)
        singleOf(::ReclaimStudentSubmissionUseCase)
        singleOf(::ResendSignUpConfirmationEmailUseCase)
        singleOf(::ResetPasswordUseCase)
        singleOf(::ReturnStudentSubmissionUseCase)
        singleOf(::SignInUseCase)
        singleOf(::SignInWithGoogleUseCase)
        singleOf(::SignOutUseCase)
        singleOf(::SignUpUseCase)
        singleOf(::TurnInStudentSubmissionUseCase)
        singleOf(::UnenrollCourseUseCase)
        singleOf(::UpdateAnnouncementUseCase)
        singleOf(::UpdateCommentUseCase)
        singleOf(::UpdateCourseUseCase)
        singleOf(::UpdateCourseWorkUseCase)
        singleOf(::UpdateMemberUseCase)
        singleOf(::UpdatePasswordUseCase)
        singleOf(::UpdateReviewDialogShownUseCase)
        singleOf(::UpdateStudentSubmissionUseCase)
        singleOf(::UploadFileUseCase)
        singleOf(::ValidateEmail)
        singleOf(::ValidateName)
        singleOf(::ValidatePassword)
        singleOf(::ValidateRepeatedPassword)
        singleOf(::ValidateTextField)
    }
