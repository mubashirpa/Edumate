package app.edumate.di

import app.edumate.data.repository.AuthenticationRepositoryImpl
import app.edumate.data.repository.CourseRepositoryImpl
import app.edumate.data.repository.CourseWorkRepositoryImpl
import app.edumate.data.repository.JsoupRepositoryImpl
import app.edumate.data.repository.MemberRepositoryImpl
import app.edumate.data.repository.StorageRepositoryImpl
import app.edumate.domain.repository.AuthenticationRepository
import app.edumate.domain.repository.CourseRepository
import app.edumate.domain.repository.CourseWorkRepository
import app.edumate.domain.repository.JsoupRepository
import app.edumate.domain.repository.MemberRepository
import app.edumate.domain.repository.StorageRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule =
    module {
        singleOf(::AuthenticationRepositoryImpl) { bind<AuthenticationRepository>() }
        singleOf(::CourseRepositoryImpl) { bind<CourseRepository>() }
        singleOf(::CourseWorkRepositoryImpl) { bind<CourseWorkRepository>() }
        singleOf(::JsoupRepositoryImpl) { bind<JsoupRepository>() }
        singleOf(::MemberRepositoryImpl) { bind<MemberRepository>() }
        singleOf(::StorageRepositoryImpl) { bind<StorageRepository>() }
    }
