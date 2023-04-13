package edumate.app.presentation.view_classwork.screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourWorkBottomSheet(
    open: Boolean,
    onDismissRequest: () -> Unit
) {
    if (open) {
        ModalBottomSheet(onDismissRequest = onDismissRequest) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
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
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Attachments",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "You have no attachments uploaded.")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    YourWorkActionButtons(
                        isEmpty = true,
                        onAddWorkClick = { /*TODO*/ },
                        onSubmitClick = { /*TODO*/ }
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