package edumate.app.presentation.create_room.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateRoomScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Create room")
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding)
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Room name")
                        },
                        shape = MaterialTheme.shapes.medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Section")
                        },
                        shape = MaterialTheme.shapes.medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Subject")
                        },
                        shape = MaterialTheme.shapes.medium
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
                Divider()
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 10.dp, vertical = 12.dp)
                ) {
                    Button(onClick = { }) {
                        Text(text = "Create")
                    }
                }
            }
        }
    }
}