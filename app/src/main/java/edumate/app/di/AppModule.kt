package edumate.app.di

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edumate.app.data.AndroidMailMatcher
import edumate.app.data.repository.*
import edumate.app.domain.repository.*
import edumate.app.domain.usecase.MailMatcher
import edumate.app.domain.usecase.validation.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Repository

    @Singleton
    @Provides
    fun provideAnnouncementsRepository(database: DatabaseReference): AnnouncementsRepository = AnnouncementsRepositoryImpl(database)

    @Singleton
    @Provides
    fun provideCoursesRepository(firestore: FirebaseFirestore): CoursesRepository = CoursesRepositoryImpl(firestore)

    @Singleton
    @Provides
    fun provideCourseWorkRepository(firestore: FirebaseFirestore): CourseWorkRepository = CourseWorkRepositoryImpl(firestore)

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
    fun provideNotificationApiService(client: HttpClient): NotificationApiService = NotificationApiServiceImpl(client)

    @Singleton
    @Provides
    fun provideStudentsRepository(firestore: FirebaseFirestore): StudentsRepository = StudentsRepositoryImpl(firestore)

    @Singleton
    @Provides
    fun provideStudentSubmissionRepository(firestore: FirebaseFirestore): StudentSubmissionRepository =
        StudentSubmissionRepositoryImpl(firestore)

    @Singleton
    @Provides
    fun provideTeachersRepository(firestore: FirebaseFirestore): TeachersRepository = TeachersRepositoryImpl(firestore)

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
                install(ContentNegotiation) {
                    json()
                }
            }
        return client
    }
}
