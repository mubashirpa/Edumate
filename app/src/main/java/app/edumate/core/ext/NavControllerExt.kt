package app.edumate.core.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController

@Composable
fun <T> NavController.GetOnceResult(
    key: String,
    onResult: (T) -> Unit,
) {
    val savedStateHandle = (currentBackStackEntry ?: return).savedStateHandle
    val result = savedStateHandle.getLiveData<T>(key).observeAsState()
    val value = result.value ?: return

    value.also {
        onResult(it)
        savedStateHandle[key] = null
    }
}
