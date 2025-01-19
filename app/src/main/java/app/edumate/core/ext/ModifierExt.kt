package app.edumate.core.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.autofill(
    autofillType: List<AutofillType>,
    onFill: (String) -> Unit,
    onReady: () -> Unit = {},
): Modifier {
    val autofill = LocalAutofill.current
    val autofillTree = LocalAutofillTree.current

    var ready by remember { mutableStateOf(value = false) }
    val autofillNode =
        remember(autofillType) {
            AutofillNode(autofillType, onFill = onFill)
        }

    LaunchedEffect(autofillNode) {
        autofillTree += autofillNode
    }

    LaunchedEffect(ready) {
        if (ready) onReady()
    }

    return this
        .onGloballyPositioned {
            autofillNode.boundingBox = it.boundsInWindow()
            ready = true
        }.onFocusChanged { focusState ->
            autofill?.run {
                if (!ready) return@onFocusChanged
                if (focusState.isFocused) {
                    requestAutofillForNode(autofillNode)
                } else {
                    cancelAutofillForNode(autofillNode)
                }
            }
        }
}
