package app.edumate.presentation.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import app.edumate.core.ext.autofill

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmailField(
    state: TextFieldState,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
    modifier: Modifier = Modifier,
    label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    shape: Shape = MaterialTheme.shapes.large,
) {
    val supportingText: @Composable (() -> Unit)? =
        errorMessage?.let {
            { Text(text = it, modifier = Modifier.clearAndSetSemantics {}) }
        }

    OutlinedTextField(
        state = state,
        modifier =
            modifier
                .autofill(
                    autofillType = listOf(AutofillType.EmailAddress),
                    onFill = { state.setTextAndPlaceCursorAtEnd(it) },
                ).semantics {
                    if (isError && errorMessage != null) error(errorMessage)
                },
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        isError = isError,
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = imeAction,
            ),
        onKeyboardAction = {
            when (imeAction) {
                ImeAction.Done -> {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }

                ImeAction.Next -> {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            }
        },
        lineLimits = TextFieldLineLimits.SingleLine,
        shape = shape,
    )
}
