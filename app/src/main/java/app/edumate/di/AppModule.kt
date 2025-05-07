package app.edumate.di

import app.edumate.data.AndroidMailMatcher
import app.edumate.data.local.dataStore
import app.edumate.domain.MailMatcher
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule =
    module {
        includes(ktorModule, repositoryModule, supabaseModule, useCaseModule, viewModelModule)
        single<MailMatcher> { AndroidMailMatcher() }
        single { androidContext().dataStore }
        single { Dispatchers.IO }
        singleOf(AppUpdateManagerFactory::create)
        singleOf(ReviewManagerFactory::create)
    }
