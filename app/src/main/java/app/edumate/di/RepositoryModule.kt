package app.edumate.di

import app.edumate.data.repository.AuthenticationRepositoryImpl
import app.edumate.data.repository.CourseRepositoryImpl
import app.edumate.data.repository.MemberRepositoryImpl
import app.edumate.domain.repository.AuthenticationRepository
import app.edumate.domain.repository.CourseRepository
import app.edumate.domain.repository.MemberRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule =
    module {
        singleOf(::AuthenticationRepositoryImpl) { bind<AuthenticationRepository>() }
        singleOf(::CourseRepositoryImpl) { bind<CourseRepository>() }
        singleOf(::MemberRepositoryImpl) { bind<MemberRepository>() }
    }
