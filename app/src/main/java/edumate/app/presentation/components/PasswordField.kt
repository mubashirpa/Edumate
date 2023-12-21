package edumate.app.presentation.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.*
import edumate.app.core.ext.autofill
import edumate.app.R.string as Strings

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    imeAction: ImeAction = ImeAction.Done,
    autofillTypes: List<AutofillType> = listOf(AutofillType.Password),
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var passwordHidden by remember { mutableStateOf(true) }
    val supportingText: @Composable (() -> Unit)? =
        if (errorMessage.isNotEmpty()) {
            { Text(text = errorMessage) }
        } else {
            null
        }

    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        modifier =
            modifier
                .autofill(
                    autofillTypes = autofillTypes,
                    onFill = { onValueChange(it) },
                )
                .semantics {
                    if (isError) error(errorMessage)
                },
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = {
            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                val visibilityIcon: ImageVector =
                    if (passwordHidden) {
                        Icons.Outlined.Visibility
                    } else {
                        Icons.Outlined.VisibilityOff
                    }
                val description =
                    if (passwordHidden) {
                        stringResource(id = Strings.show_password)
                    } else {
                        stringResource(id = Strings.hide_password)
                    }
                Icon(imageVector = visibilityIcon, contentDescription = description)
            }
        },
        supportingText = supportingText,
        isError = isError,
        visualTransformation =
            if (passwordHidden) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
        keyboardOptions =
            KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Password,
                imeAction = imeAction,
            ),
        keyboardActions =
            KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                },
            ),
        singleLine = true,
        shape = MaterialTheme.shapes.large,
    )
}
