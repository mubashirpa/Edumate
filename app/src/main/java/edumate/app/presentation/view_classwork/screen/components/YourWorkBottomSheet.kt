package edumate.app.presentation.view_classwork.screen.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edumate.app.presentation.view_classwork.ViewClassworkUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourWorkBottomSheet(
    uiState: ViewClassworkUiState,
    onDismissRequest: () -> Unit,
    onAddAttachmentClick: () -> Unit,
    onRemoveAttachmentClick: (Int) -> Unit,
    onSubmitClick: () -> Unit
) {
    if (uiState.openYourWorkBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = SheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .animateContentSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Your work",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text(
                        text = "Attachments",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (uiState.attachments.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(128.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "You have no attachments uploaded.")
                        }
                    } else {
                        uiState.attachments.onEachIndexed { index, attachment ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = attachment.driveFile?.title
                                            ?: attachment.driveFile?.url.orEmpty(),
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                },
                                trailingContent = {
                                    IconButton(onClick = { onRemoveAttachmentClick(index) }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = null
                                        )
                                    }
                                }
                            )
                            if (index < uiState.attachments.lastIndex) {
                                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    YourWorkActionButtons(
                        isEmpty = uiState.attachments.isEmpty(),
                        onAddWorkClick = onAddAttachmentClick,
                        onSubmitClick = onSubmitClick
                    )
                }
            }
        }
    }
}

@Composable
private fun YourWorkActionButtons(
    isEmpty: Boolean,
    onAddWorkClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    if (isEmpty) {
        Button(
            onClick = onAddWorkClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Add work",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Add work")
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = onSubmitClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Mark as done")
        }
    } else {
        OutlinedButton(
            onClick = onAddWorkClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Add work",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Add work")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onSubmitClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Hand in")
        }
    }
}