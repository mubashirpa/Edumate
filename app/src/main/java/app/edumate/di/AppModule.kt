package app.edumate.di

import org.koin.dsl.module

val appModule =
    module {
        includes(ktorModule, repositoryModule, supabaseModule, useCaseModule, viewModelModule)
    }
