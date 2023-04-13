package edumate.app.presentation.stream.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import edumate.app.presentation.components.ComingSoon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamScreen() {
    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            ComingSoon()
        }
        val open = remember { mutableStateOf(false) }
        if (open.value) {
            ModalBottomSheet(onDismissRequest = { open.value = false }) {
                Text(text = "hello")
                Text(text = "hello")
                Text(text = "hello")
                Text(text = "hello")
                Text(text = "hello")
                Text(text = "hello")
                Text(text = "hello")
                Text(text = "hello")
            }
        }
    }
}