package edumate.app.presentation.view_student_work.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import edumate.app.R
import edumate.app.core.utils.DevicePreviews
import edumate.app.presentation.components.ErrorScreen

@OptIn(ExperimentalMaterial3Api::class)
@DevicePreviews
@Composable
fun ViewStudentWorkScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(text = "Assignment")
            },
            navigationIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.navigate_up)
                    )
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            ListItem(
                headlineContent = {
                    Text(text = "Mubashir P A")
                },
                supportingContent = {
                    Text(text = "Marked")
                }
            )
            ErrorScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                errorMessage = "No files attached"
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {
                    },
                    modifier = Modifier.weight(1f),
                    label = {
                        Text(text = "Mark")
                    },
                    suffix = {
                        Text(text = "/100")
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Return")
                }
            }
        }
    }
}