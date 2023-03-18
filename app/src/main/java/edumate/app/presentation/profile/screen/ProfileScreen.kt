package edumate.app.presentation.profile.screen

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import edumate.app.presentation.components.ComingSoon
import edumate.app.presentation.profile.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    ComingSoon()
    Button(onClick = { viewModel.signOut() }) {
        Text(text = "Log out")
    }
}