package edumate.app.di

import android.app.Application
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edumate.app.BuildConfig
import edumate.app.data.AndroidMailMatcher
import edumate.app.data.repository.*
import edumate.app.domain.repository.*
import edumate.app.domain.usecase.MailMatcher
import edumate.app.domain.usecase.validation.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Repository

    @Singleton
    @Provides
    fun provideAnnouncementsRepository(httpClient: HttpClient): AnnouncementsRepository = AnnouncementsRepositoryImpl(httpClient)

    @Singleton
    @Provides
    fun provideCoursesRepository(httpClient: HttpClient): CoursesRepository = CoursesRepositoryImpl(httpClient)

    @Singleton
    @Provides
    fun provideCourseWorkRepository(httpClient: HttpClient): CourseWorkRepository = CourseWorkRepositoryImpl(httpClient)

    @Singleton
    @Provides
    fun provideFirebaseAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): FirebaseAuthRepository = FirebaseAuthRepositoryImpl(firebaseAuth, firestore)

    @Singleton
    @Provides
    fun provideFirebaseStorageRepository(storageReference: StorageReference): FirebaseStorageRepository =
        FirebaseStorageRepositoryImpl(storageReference)

    @Singleton
    @Provides
    fun provideJsoupRepository(): JsoupRepository = JsoupRepositoryImpl()

    @Singleton
    @Provides
    fun provideMeetingsRepository(database: DatabaseReference): MeetingsRepository = MeetingsRepositoryImpl(database)

    @Singleton
    @Provides
    fun provideNotificationApiService(client: HttpClient): NotificationService = NotificationServiceImpl(client)

    @Singleton
    @Provides
    fun provideStudentsRepository(httpClient: HttpClient): StudentsRepository = StudentsRepositoryImpl(httpClient)

    @Singleton
    @Provides
    fun provideStudentSubmissionRepository(httpClient: HttpClient): StudentSubmissionRepository =
        StudentSubmissionRepositoryImpl(httpClient)

    @Singleton
    @Provides
    fun provideTeachersRepository(httpClient: HttpClient): TeachersRepository = TeachersRepositoryImpl(httpClient)

    @Singleton
    @Provides
    fun provideUserPreferencesRepository(applicationContext: Application): UserPreferencesRepository =
        UserPreferencesRepositoryImpl(applicationContext)

    // Form validation

    @Provides
    @Singleton
    fun provideMailMatcher(): MailMatcher = AndroidMailMatcher()

    @Provides
    @Singleton
    fun provideValidateEmail(mailMatcher: MailMatcher): ValidateEmail = ValidateEmail(mailMatcher)

    @Provides
    @Singleton
    fun provideValidateName(): ValidateName = ValidateName()

    @Provides
    @Singleton
    fun provideValidatePassword(): ValidatePassword = ValidatePassword()

    @Provides
    @Singleton
    fun provideValidateRepeatedPassword(): ValidateRepeatedPassword = ValidateRepeatedPassword()

    @Provides
    @Singleton
    fun provideValidateTextField(): ValidateTextField = ValidateTextField()

    // Ktor client

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        val client =
            HttpClient(CIO) {
                expectSuccess = true
                install(ContentNegotiation) {
                    json(
                        Json {
                            isLenient = true
                            ignoreUnknownKeys = true
                            useAlternativeNames = false
                        },
                    )
                }
            }
        return client
    }

    // Gemini AI

    @Singleton
    @Provides
    fun provideGenerativeModel(): GenerativeModel {
        val config =
            generationConfig {
                temperature = 0.7f
            }

        return GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = config,
        )
    }
}
