package edumate.app.presentation.classwork.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.LiveHelp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edumate.app.presentation.classwork.ClassworkUiEvent
import edumate.app.presentation.classwork.ClassworkViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassworkScreen(
    viewModel: ClassworkViewModel = hiltViewModel()
) {
    val windowInsets: WindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Bottom)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            content = {
                items(viewModel.uiState.classWorks) { classWork ->
                    ListItem(
                        headlineContent = {
                            Text(text = classWork.title)
                        },
                        modifier = Modifier.clickable {
                        },
                        supportingContent = {
                            val format = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                            val postedDate: String = format.format(classWork.creationTime!!)
                            Text(text = "Posted $postedDate")
                        },
                        leadingContent = {
                            Icon(imageVector = Icons.Default.Assignment, contentDescription = null)
                        }
                    )
                }
            }
        )

        FloatingActionButton(
            onClick = {
                viewModel.onEvent(ClassworkUiEvent.OnOpenFabMenuChange(true))
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .imePadding()
                .padding(16.dp)
        ) { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
    }

    // TODO("Fix alignment")
    if (viewModel.uiState.openFabMenu) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
            }
        ) {
            ListItem(
                headlineContent = { Text(text = "Assignment") },
                modifier = Modifier.clickable {
                    viewModel.onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
                },
                leadingContent = {
                    Icon(imageVector = Icons.Outlined.Assignment, contentDescription = null)
                }
            )
            ListItem(
                headlineContent = { Text(text = "Question") },
                modifier = Modifier.clickable {
                    viewModel.onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
                },
                leadingContent = {
                    Icon(imageVector = Icons.Outlined.LiveHelp, contentDescription = null)
                }
            )
            ListItem(
                headlineContent = { Text(text = "Material") },
                modifier = Modifier.clickable {
                    viewModel.onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
                },
                leadingContent = {
                    Icon(imageVector = Icons.Outlined.Book, contentDescription = null)
                }
            )
            Spacer(
                modifier = Modifier
                    .height(windowInsets.asPaddingValues().calculateBottomPadding())
            )
        }
    }
}

@Composable
@Preview
fun CreateAssignment() {
    Column(modifier = Modifier.fillMaxSize()) {
        ListItem(
            headlineContent = {
                OutlinedTextField(value = "Assignment title", onValueChange = {})
            }
        )
        ListItem(
            headlineContent = {
                OutlinedTextField(value = "Description", onValueChange = {})
            },
            leadingContent = {
                Icon(imageVector = Icons.Default.Description, contentDescription = null)
            }
        )
        ListItem(
            headlineContent = {
                OutlinedTextField(value = "Add attachment", onValueChange = {})
            },
            leadingContent = {
                Icon(imageVector = Icons.Default.Attachment, contentDescription = null)
            }
        )
    }
}