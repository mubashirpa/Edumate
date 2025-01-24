package app.edumate.di

import app.edumate.data.AndroidMailMatcher
import app.edumate.domain.MailMatcher
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
import app.edumate.domain.usecase.courses.CreateCourseUseCase
import app.edumate.domain.usecase.courses.GetCourseUseCase
import app.edumate.domain.usecase.courses.GetCoursesUseCase
import app.edumate.domain.usecase.courses.JoinCourseUseCase
import app.edumate.domain.usecase.courses.UpdateCourseUseCase
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
        singleOf(::CreateCourseUseCase)
        singleOf(::GetCourseUseCase)
        singleOf(::GetCoursesUseCase)
        singleOf(::GetCurrentUserUseCase)
        singleOf(::GetSignInInfoUseCase)
        singleOf(::IsUserLoggedInUseCase)
        singleOf(::JoinCourseUseCase)
        singleOf(::ResendSignUpConfirmationEmailUseCase)
        singleOf(::ResetPasswordUseCase)
        singleOf(::SignInUseCase)
        singleOf(::SignInWithGoogleUseCase)
        singleOf(::SignOutUseCase)
        singleOf(::SignUpUseCase)
        singleOf(::UpdateCourseUseCase)
        singleOf(::UpdatePasswordUseCase)
        singleOf(::ValidateEmail)
        singleOf(::ValidateName)
        singleOf(::ValidatePassword)
        singleOf(::ValidateRepeatedPassword)
        singleOf(::ValidateTextField)
        single<MailMatcher> { AndroidMailMatcher() }
        single { Dispatchers.IO }
    }
