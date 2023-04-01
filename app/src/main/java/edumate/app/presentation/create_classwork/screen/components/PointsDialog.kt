package edumate.app.presentation.create_classwork.screen.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PointsDialog(
    openDialog: Boolean,
    currentPoint: String?,
    onConfirmClick: (points: String?) -> Unit,
    onDismissRequest: () -> Unit
) {
    if (openDialog) {
        var point by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(currentPoint ?: "100"))
        }
        val tempPoint: MutableState<String?> = remember { mutableStateOf("") }

        point = TextFieldValue(currentPoint ?: "100")
        tempPoint.value = currentPoint

        val confirmEnabled = derivedStateOf { point.text.isNotBlank() }

        AlertDialog(onDismissRequest = onDismissRequest) {
            val focusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current

            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = AlertDialogDefaults.shape,
                color = AlertDialogDefaults.containerColor,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = stringResource(id = Strings.change_point_value),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = tempPoint.value != null,
                                onClick = { tempPoint.value = point.text },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = tempPoint.value != null,
                            onClick = null
                        )
                        BasicTextField(
                            value = point,
                            onValueChange = {
                                if (it.text.length <= 3) {
                                    point = it
                                    tempPoint.value = it.text
                                }
                            },
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .widthIn(max = 500.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = stringResource(id = Strings._points, ""),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = tempPoint.value == null,
                                onClick = { tempPoint.value = null },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = tempPoint.value == null,
                            onClick = null
                        )
                        Text(
                            text = stringResource(id = Strings.unmarked),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.align(Alignment.End)) {
                        TextButton(onClick = onDismissRequest) {
                            Text(stringResource(id = Strings.cancel))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                onDismissRequest()
                                onConfirmClick(tempPoint.value?.trim())
                            },
                            enabled = if (tempPoint.value != null) confirmEnabled.value else true
                        ) {
                            Text(stringResource(id = Strings.save))
                        }
                    }
                }
            }
        }
    }
}