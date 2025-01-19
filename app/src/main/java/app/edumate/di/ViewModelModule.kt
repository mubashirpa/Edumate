package app.edumate.di

import app.edumate.presentation.MainViewModel
import app.edumate.presentation.profile.ProfileViewModel
import app.edumate.presentation.resetPassword.ResetPasswordViewModel
import app.edumate.presentation.signIn.SignInViewModel
import app.edumate.presentation.signUp.SignUpViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModelOf(::MainViewModel)
        viewModelOf(::ProfileViewModel)
        viewModelOf(::ResetPasswordViewModel)
        viewModelOf(::SignInViewModel)
        viewModelOf(::SignUpViewModel)
    }
