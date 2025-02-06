package app.edumate.di

import app.edumate.data.repository.AnnouncementRepositoryImpl
import app.edumate.data.repository.AuthenticationRepositoryImpl
import app.edumate.data.repository.CommentRepositoryImpl
import app.edumate.data.repository.CourseRepositoryImpl
import app.edumate.data.repository.CourseWorkRepositoryImpl
import app.edumate.data.repository.JsoupRepositoryImpl
import app.edumate.data.repository.MemberRepositoryImpl
import app.edumate.data.repository.StorageRepositoryImpl
import app.edumate.data.repository.StudentSubmissionRepositoryImpl
import app.edumate.domain.repository.AnnouncementRepository
import app.edumate.domain.repository.AuthenticationRepository
import app.edumate.domain.repository.CommentRepository
import app.edumate.domain.repository.CourseRepository
import app.edumate.domain.repository.CourseWorkRepository
import app.edumate.domain.repository.JsoupRepository
import app.edumate.domain.repository.MemberRepository
import app.edumate.domain.repository.StorageRepository
import app.edumate.domain.repository.StudentSubmissionRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule =
    module {
        singleOf(::AnnouncementRepositoryImpl) { bind<AnnouncementRepository>() }
        singleOf(::AuthenticationRepositoryImpl) { bind<AuthenticationRepository>() }
        singleOf(::CommentRepositoryImpl) { bind<CommentRepository>() }
        singleOf(::CourseRepositoryImpl) { bind<CourseRepository>() }
        singleOf(::CourseWorkRepositoryImpl) { bind<CourseWorkRepository>() }
        singleOf(::JsoupRepositoryImpl) { bind<JsoupRepository>() }
        singleOf(::MemberRepositoryImpl) { bind<MemberRepository>() }
        singleOf(::StorageRepositoryImpl) { bind<StorageRepository>() }
        singleOf(::StudentSubmissionRepositoryImpl) { bind<StudentSubmissionRepository>() }
    }
