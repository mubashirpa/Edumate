package edumate.app.presentation.create_classwork.screen

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import edumate.app.presentation.components.ComingSoon

@Composable
fun CreateClassworkScreen(
    workType: String
) {
    LaunchedEffect(true) {
        Log.d("hello", workType)
    }

    ComingSoon()
}