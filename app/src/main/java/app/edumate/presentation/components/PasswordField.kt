package app.edumate.presentation.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import app.edumate.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordField(
    state: TextFieldState,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
    modifier: Modifier = Modifier,
    fieldContentType: ContentType = ContentType.Password,
    label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    shape: Shape = MaterialTheme.shapes.large,
) {
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    val supportingText: @Composable (() -> Unit)? =
        errorMessage?.let {
            { Text(text = it, modifier = Modifier.clearAndSetSemantics {}) }
        }

    OutlinedSecureTextField(
        state = state,
        modifier =
            modifier.semantics {
                contentType = fieldContentType
                if (isError && errorMessage != null) error(errorMessage)
            },
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = {
            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                val visibilityIcon =
                    if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                // Provide localized description for accessibility services
                val description =
                    if (passwordHidden) {
                        stringResource(R.string.show_password)
                    } else {
                        stringResource(R.string.hide_password)
                    }
                Icon(
                    imageVector = visibilityIcon,
                    contentDescription = description,
                )
            }
        },
        supportingText = supportingText,
        isError = isError,
        textObfuscationMode =
            if (passwordHidden) {
                TextObfuscationMode.RevealLastTyped
            } else {
                TextObfuscationMode.Visible
            },
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Password,
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
        shape = shape,
    )
}
