package app.edumate.presentation.createCourseWork.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import app.edumate.R

@Composable
fun MultipleChoiceContent(
    choices: List<String>,
    onAddChoice: (String) -> Unit,
    onChoiceChange: (Int, String) -> Unit,
    onRemoveChoice: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    Column(modifier = modifier) {
        choices.forEachIndexed { index, choice ->
            var value by remember {
                mutableStateOf(TextFieldValue(text = choice, selection = TextRange(choice.length)))
            }
            val interactionSource = remember { MutableInteractionSource() }
            val isFocused by interactionSource.collectIsFocusedAsState()

            LaunchedEffect(isFocused) {
                val endRange = if (isFocused) value.text.length else 0
                value = value.copy(selection = TextRange(start = 0, end = endRange))
            }

            LaunchedEffect(choice) {
                if (value.text != choice) {
                    value = TextFieldValue(text = choice, selection = TextRange(choice.length))
                }
            }

            ListItem(
                headlineContent = {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = false,
                            onClick = null,
                        )
                        BasicTextField(
                            value = value,
                            onValueChange = {
                                value = it
                                onChoiceChange(index, it.text)
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                            textStyle =
                                MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                ),
                            keyboardOptions =
                                KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    autoCorrectEnabled = true,
                                    imeAction = ImeAction.Done,
                                ),
                            keyboardActions =
                                KeyboardActions(
                                    onDone = {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                    },
                                ),
                            interactionSource = interactionSource,
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        )
                    }
                },
                trailingContent = {
                    if (choices.size > 1) {
                        IconButton(onClick = { onRemoveChoice(index) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                            )
                        }
                    }
                },
            )
            HorizontalDivider()
        }
        ListItem(
            headlineContent = {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = false,
                        onClick = null,
                    )
                    Text(
                        text = stringResource(id = R.string.add_option),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }
            },
            modifier =
                Modifier.clickable {
                    onAddChoice(context.getString(R.string.option_, "${choices.size + 1}"))
                },
        )
    }
}
