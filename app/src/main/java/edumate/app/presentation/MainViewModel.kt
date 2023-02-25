package edumate.app.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.domain.repository.FirebaseAuthRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {
    val isLoggedIn get() = firebaseAuthRepository.hasUser
}