package edumate.app.presentation.create_classwork.screen

import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CreateClassworkScreen(
    workType: String
) {
    when (workType) {
        "ASSIGNMENT" -> {
            ContentAssignment()
        }
        "SHORT_ANSWER_QUESTION" -> {
            ContentQuestion()
        }
        "COURSE_WORK_TYPE_UNSPECIFIED" -> {
            ContentMaterial()
        }
    }
}

@Composable
private fun ContentAssignment() {
    var description by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    val iconButtonPlaceholder: @Composable () -> Unit = {
        Spacer(modifier = Modifier.size(48.dp))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            ListItem(
                headlineContent = {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Assignment title")
                        }
                    )
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.Assignment, contentDescription = null)
                },
                trailingContent = iconButtonPlaceholder
            )
            ListItem(
                headlineContent = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                            .horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        ElevatedSuggestionChip(
                            onClick = {},
                            label = { Text(text = "Name") }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        ElevatedSuggestionChip(
                            onClick = {},
                            label = { Text(text = "All students") }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.People, contentDescription = null)
                },
                trailingContent = iconButtonPlaceholder
            )
            ListItem(
                headlineContent = {
                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Description")
                        }
                    )
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null)
                },
                trailingContent = iconButtonPlaceholder
            )
            Item(
                title = "Add attachment",
                leadingIcon = Icons.Default.Attachment,
                onClick = {}
            )
            Item(
                title = "100 points",
                leadingIcon = Icons.Default.PlaylistAddCheck,
                trailingContent = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                    }
                },
                onClick = {}
            )
            Item(
                title = "Due date",
                leadingIcon = Icons.Default.CalendarToday,
                onClick = {}
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Assign")
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentQuestion() {
    var description by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    val iconButtonPlaceholder: @Composable () -> Unit = {
        Spacer(modifier = Modifier.size(48.dp))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            ListItem(
                headlineContent = {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Question title")
                        }
                    )
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.LiveHelp, contentDescription = null)
                },
                trailingContent = iconButtonPlaceholder
            )
            ListItem(
                headlineContent = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                            .horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        ElevatedSuggestionChip(
                            onClick = {},
                            label = { Text(text = "Name") }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        ElevatedSuggestionChip(
                            onClick = {},
                            label = { Text(text = "All students") }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.People, contentDescription = null)
                },
                trailingContent = iconButtonPlaceholder
            )
            ListItem(
                headlineContent = {
                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Description")
                        }
                    )
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null)
                },
                trailingContent = iconButtonPlaceholder
            )

            val options = listOf("Short answer", "Multiple choice")
            var expanded by remember { mutableStateOf(false) }
            var selectedOptionText by remember { mutableStateOf(options[0]) }
            ListItem(
                headlineContent = {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedOptionText,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded
                                )
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            options.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        selectedOptionText = selectionOption
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.Quiz, contentDescription = null)
                },
                trailingContent = iconButtonPlaceholder
            )

            if (selectedOptionText == options[1]) {
                Item(title = "Add answers") {
                }
            }

            Item(
                title = "Add attachment",
                leadingIcon = Icons.Default.Attachment,
                onClick = {}
            )
            Item(
                title = "100 points",
                leadingIcon = Icons.Default.PlaylistAddCheck,
                trailingContent = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                    }
                },
                onClick = {}
            )
            Item(
                title = "Due date",
                leadingIcon = Icons.Default.CalendarToday,
                onClick = {}
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Ask")
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ContentMaterial() {
    var description by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    val iconButtonPlaceholder: @Composable () -> Unit = {
        Spacer(modifier = Modifier.size(48.dp))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            ListItem(
                headlineContent = {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Material title")
                        }
                    )
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.Book, contentDescription = null)
                },
                trailingContent = iconButtonPlaceholder
            )
            ListItem(
                headlineContent = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                            .horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        ElevatedSuggestionChip(
                            onClick = {},
                            label = { Text(text = "Name") }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        ElevatedSuggestionChip(
                            onClick = {},
                            label = { Text(text = "All students") }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.People, contentDescription = null)
                },
                trailingContent = iconButtonPlaceholder
            )
            ListItem(
                headlineContent = {
                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Description")
                        }
                    )
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null)
                },
                trailingContent = iconButtonPlaceholder
            )
            Item(
                title = "Add attachment",
                leadingIcon = Icons.Default.Attachment,
                onClick = {}
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Post")
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun Item(
    title: String,
    leadingIcon: ImageVector? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .clip(MaterialTheme.shapes.extraSmall)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        leadingContent = {
            if (leadingIcon != null) {
                Icon(imageVector = leadingIcon, contentDescription = null)
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
        },
        trailingContent = trailingContent ?: { Spacer(modifier = Modifier.size(48.dp)) }
    )
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    device = "id:pixel_6_pro",
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun CreateClassworkScreenPreview() {
    CreateClassworkScreen("COURSE_WORK_TYPE_UNSPECIFIED")
}