package app.edumate.di

import app.edumate.presentation.courseDetails.CourseDetailsViewModel
import app.edumate.presentation.courseWork.CourseWorkViewModel
import app.edumate.presentation.createCourse.CreateCourseViewModel
import app.edumate.presentation.createCourseWork.CreateCourseWorkViewModel
import app.edumate.presentation.home.HomeViewModel
import app.edumate.presentation.main.MainViewModel
import app.edumate.presentation.newPassword.NewPasswordViewModel
import app.edumate.presentation.people.PeopleViewModel
import app.edumate.presentation.profile.ProfileViewModel
import app.edumate.presentation.resetPassword.ResetPasswordViewModel
import app.edumate.presentation.settings.SettingsViewModel
import app.edumate.presentation.signIn.SignInViewModel
import app.edumate.presentation.signUp.SignUpViewModel
import app.edumate.presentation.stream.StreamViewModel
import app.edumate.presentation.studentWork.StudentWorkViewModel
import app.edumate.presentation.viewCourseWork.ViewCourseWorkViewModel
import app.edumate.presentation.viewStudentSubmission.ViewStudentSubmissionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModelOf(::CourseDetailsViewModel)
        viewModelOf(::CourseWorkViewModel)
        viewModelOf(::CreateCourseViewModel)
        viewModelOf(::CreateCourseWorkViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::MainViewModel)
        viewModelOf(::NewPasswordViewModel)
        viewModelOf(::PeopleViewModel)
        viewModelOf(::ProfileViewModel)
        viewModelOf(::ResetPasswordViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::SignInViewModel)
        viewModelOf(::SignUpViewModel)
        viewModelOf(::StreamViewModel)
        viewModelOf(::StudentWorkViewModel)
        viewModelOf(::ViewCourseWorkViewModel)
        viewModelOf(::ViewStudentSubmissionViewModel)
    }
