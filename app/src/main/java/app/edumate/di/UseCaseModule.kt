package app.edumate.di

import app.edumate.data.AndroidMailMatcher
import app.edumate.domain.MailMatcher
import app.edumate.domain.usecase.authentication.GetSignInInfoUseCase
import app.edumate.domain.usecase.authentication.IsUserLoggedInUseCase
import app.edumate.domain.usecase.authentication.ResetPasswordUseCase
import app.edumate.domain.usecase.authentication.SignInUseCase
import app.edumate.domain.usecase.authentication.SignInWithGoogleUseCase
import app.edumate.domain.usecase.authentication.SignUpUseCase
import app.edumate.domain.usecase.validation.ValidateEmail
import app.edumate.domain.usecase.validation.ValidateName
import app.edumate.domain.usecase.validation.ValidatePassword
import app.edumate.domain.usecase.validation.ValidateRepeatedPassword
import app.edumate.domain.usecase.validation.ValidateTextField
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule =
    module {
        singleOf(::SignUpUseCase)
        singleOf(::SignInUseCase)
        singleOf(::SignInWithGoogleUseCase)
        singleOf(::ResetPasswordUseCase)
        singleOf(::GetSignInInfoUseCase)
        singleOf(::IsUserLoggedInUseCase)
        singleOf(::ValidateEmail)
        singleOf(::ValidateName)
        singleOf(::ValidatePassword)
        singleOf(::ValidateRepeatedPassword)
        singleOf(::ValidateTextField)
        single<MailMatcher> { AndroidMailMatcher() }
    }
