package app.edumate.presentation.components

import android.webkit.URLUtil
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.edumate.R

@Composable
fun AddLinkDialog(
    open: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmClick: (link: String) -> Unit,
) {
    if (open) {
        val link = rememberTextFieldState()
        val confirmEnabled by remember { derivedStateOf { URLUtil.isValidUrl("${link.text}") } }

        Dialog(onDismissRequest = onDismissRequest) {
            val focusRequester = remember { FocusRequester() }
            val focusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current

            Surface(
                shape = AlertDialogDefaults.shape,
                color = AlertDialogDefaults.containerColor,
                tonalElevation = AlertDialogDefaults.TonalElevation,
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = stringResource(id = R.string.add_link),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        state = link,
                        modifier = Modifier.focusRequester(focusRequester),
                        label = {
                            Text(text = stringResource(id = R.string.link))
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Done,
                            ),
                        onKeyboardAction = {
                            if (confirmEnabled) {
                                onDismissRequest()
                                onConfirmClick("${link.text}")
                            } else {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        },
                        lineLimits = TextFieldLineLimits.SingleLine,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.align(Alignment.End)) {
                        TextButton(onClick = onDismissRequest) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                onDismissRequest()
                                onConfirmClick("${link.text}")
                            },
                            enabled = confirmEnabled,
                        ) {
                            Text(text = stringResource(id = R.string.add))
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}
