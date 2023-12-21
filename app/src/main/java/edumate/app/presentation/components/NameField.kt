package edumate.app.presentation.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import edumate.app.core.ext.autofill

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NameField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    imeAction: ImeAction = ImeAction.Done,
    autofillTypes: List<AutofillType> = listOf(AutofillType.PersonFullName),
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
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
        readOnly = readOnly,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        isError = isError,
        keyboardOptions =
            KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
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
