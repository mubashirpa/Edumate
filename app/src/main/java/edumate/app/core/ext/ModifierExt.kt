package edumate.app.core.ext

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.unit.dp

private const val TAG = "ModifierExt"

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.autofill(
    autofillTypes: List<AutofillType>,
    onFill: (String) -> Unit,
) = composed {
    val autofill = LocalAutofill.current
    val autofillNode =
        AutofillNode(
            autofillTypes = autofillTypes,
            onFill = onFill,
        )
    val autofillTree = LocalAutofillTree.current
    autofillTree += autofillNode

    onGloballyPositioned { coordinates ->
        autofillNode.boundingBox = coordinates.boundsInWindow()
    }
    onFocusChanged { focusState ->
        autofill?.run {
            if (focusState.isFocused) {
                // TODO("Fix: requestAutofill called before onChildPositioned()")
                try {
                    requestAutofillForNode(autofillNode)
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString(), e)
                }
            } else {
                cancelAutofillForNode(autofillNode)
            }
        }
    }
}

/**
 * Support wide screen by making the content width max 840dp, centered horizontally.
 */
fun Modifier.supportWideScreen() =
    this
        .fillMaxWidth()
        .wrapContentWidth(align = Alignment.CenterHorizontally)
        .widthIn(max = 840.dp)
