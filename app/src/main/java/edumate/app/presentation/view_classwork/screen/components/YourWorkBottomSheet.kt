package edumate.app.presentation.view_classwork.screen.components

import android.text.format.DateUtils
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
import edumate.app.core.DataState
import edumate.app.domain.model.student_submission.SubmissionState
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.view_classwork.ViewClassworkUiState
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourWorkBottomSheet(
    uiState: ViewClassworkUiState,
    onDismissRequest: () -> Unit,
    onAddAttachmentClick: () -> Unit,
    onRemoveAttachmentClick: (Int) -> Unit,
    onSubmitClick: () -> Unit,
    onUnSubmitClick: () -> Unit
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your work",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge
                    )
                    TopBarTrailingContent(uiState = uiState)
                }

                when (uiState.yourWorkDataState) {
                    is DataState.ERROR -> {
                        ErrorScreen(errorMessage = uiState.yourWorkDataState.message.asString())
                    }

                    DataState.LOADING -> {
                        LoadingIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(128.dp)
                        )
                    }

                    DataState.UNKNOWN -> {}

                    else -> {
                        Column(
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 10.dp
                            )
                        ) {
                            val attachments =
                                uiState.studentSubmissionAttachments

                            Text(
                                text = "Attachments",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            if (attachments.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(128.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "You have no attachments uploaded.")
                                }
                            } else {
                                attachments.onEachIndexed { index, attachment ->
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
                                    if (index < attachments.lastIndex) {
                                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            YourWorkActionButtons(
                                state = uiState.studentSubmissionState,
                                attachmentsSize = attachments.size,
                                onAddWorkClick = onAddAttachmentClick,
                                onSubmitClick = onSubmitClick,
                                onUnSubmitClick = onUnSubmitClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YourWorkActionButtons(
    state: SubmissionState?,
    attachmentsSize: Int,
    onAddWorkClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onUnSubmitClick: () -> Unit
) {
    when (state) {
        SubmissionState.TURNED_IN -> {
            Button(
                onClick = onUnSubmitClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Un-submit")
            }
        }

        SubmissionState.RETURNED -> {
            Button(
                onClick = onUnSubmitClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Un-submit")
            }
        }

        else -> {
            if (attachmentsSize == 0) {
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
    }
}

@Composable
private fun TopBarTrailingContent(
    uiState: ViewClassworkUiState
) {
    val dueDate = uiState.classwork.dueTime
    val date = if (dueDate != null) {
        DateUtils.getRelativeTimeSpanString(dueDate.time)
    } else {
        null
    }

    Column {
        when (uiState.studentSubmissionState) {
            SubmissionState.TURNED_IN -> {
                Text(
                    text = "Handed in",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (uiState.studentSubmissionLate) {
                    Text(
                        text = "Done late",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            SubmissionState.RETURNED -> {
                Text(
                    text = "${uiState.studentSubmissionPoint}/${uiState.classwork.maxPoints}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (uiState.studentSubmissionLate) {
                    Text(
                        text = "Done late",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            SubmissionState.RECLAIMED_BY_STUDENT -> {
                if (uiState.studentSubmissionPoint != null) {
                    Text(
                        text = "${uiState.studentSubmissionPoint}/${uiState.classwork.maxPoints}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (uiState.studentSubmissionLate) {
                        Text(
                            text = "Done late",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                } else if (dueDate != null) {
                    if (dueDate.before(Date())) {
                        Text(
                            text = "Missing",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else if (date != null) {
                        Text(
                            text = "Due $date",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Text(
                        text = "No due date",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            else -> {
                if (dueDate != null) {
                    if (dueDate.before(Date())) {
                        Text(
                            text = "Missing",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else if (date != null) {
                        Text(
                            text = "Due $date",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Text(
                        text = "No due date",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}