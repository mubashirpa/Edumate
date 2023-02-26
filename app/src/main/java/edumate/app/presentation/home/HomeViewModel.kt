package edumate.app.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.domain.usecase.authentication.SignOutUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.SignOut -> {
                signOutUseCase()
            }
        }
    }
}