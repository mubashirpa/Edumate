package edumate.app.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Repository

    @Singleton
    @Provides
    fun provideFirebaseAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): FirebaseAuthRepository =
        FirebaseAuthRepositoryImpl(firebaseAuth, firestore)

    @Singleton
    @Provides
    fun provideFirebaseStorageRepository(storageReference: StorageReference): FirebaseStorageRepository =
        FirebaseStorageRepositoryImpl(storageReference)

    @Singleton
    @Provides
    fun provideDynamicLinksRepository(dynamicLinks: FirebaseDynamicLinks): DynamicLinksRepository =
        DynamicLinksRepositoryImpl(dynamicLinks)

    @Singleton
    @Provides
    fun provideCoursesRepository(firestore: FirebaseFirestore): CoursesRepository =
        CoursesRepositoryImpl(firestore)

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
}