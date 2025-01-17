package app.edumate.di

import org.koin.dsl.module

val appModule =
    module {
        includes(ktorModule, supabaseModule)
    }
