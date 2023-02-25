package edumate.app.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edumate.app.data.AndroidMailMatcher
import edumate.app.data.repository.FirebaseAuthRepositoryImpl
import edumate.app.data.repository.FirebaseStorageRepositoryImpl
import edumate.app.domain.repository.FirebaseAuthRepository
import edumate.app.domain.repository.FirebaseStorageRepository
import edumate.app.domain.usecase.MailMatcher
import edumate.app.domain.usecase.validation.ValidateEmail
import edumate.app.domain.usecase.validation.ValidateName
import edumate.app.domain.usecase.validation.ValidatePassword
import edumate.app.domain.usecase.validation.ValidateRepeatedPassword
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Repository

    @Singleton
    @Provides
    fun provideFirebaseAuthRepository(firebaseAuth: FirebaseAuth): FirebaseAuthRepository =
        FirebaseAuthRepositoryImpl(firebaseAuth)

    @Singleton
    @Provides
    fun provideFirebaseStorageRepository(storageReference: StorageReference): FirebaseStorageRepository =
        FirebaseStorageRepositoryImpl(storageReference)

    // Form validation

    @Provides
    @Singleton
    fun provideValidateEmail(mailMatcher: MailMatcher): ValidateEmail = ValidateEmail(mailMatcher)

    @Provides
    @Singleton
    fun provideMailMatcher(): MailMatcher = AndroidMailMatcher()

    @Provides
    @Singleton
    fun provideValidateName(): ValidateName = ValidateName()

    @Provides
    @Singleton
    fun provideValidatePassword(): ValidatePassword = ValidatePassword()

    @Provides
    @Singleton
    fun provideValidateRepeatedPassword(): ValidateRepeatedPassword = ValidateRepeatedPassword()
}