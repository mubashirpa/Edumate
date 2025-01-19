package app.edumate.di

import app.edumate.data.repository.AuthenticationRepositoryImpl
import app.edumate.domain.repository.AuthenticationRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule =
    module {
        singleOf(::AuthenticationRepositoryImpl) { bind<AuthenticationRepository>() }
    }
