package edumate.app.presentation.create_room.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edumate.app.presentation.create_room.CreateRoomUiEvent
import edumate.app.presentation.create_room.CreateRoomViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateRoomScreen(
    viewModel: CreateRoomViewModel = hiltViewModel()
) {
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
                        value = viewModel.uiState.name,
                        onValueChange = {
                            viewModel.onEvent(CreateRoomUiEvent.NameChanged(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Room name")
                        },
                        shape = MaterialTheme.shapes.medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = viewModel.uiState.section,
                        onValueChange = {
                            viewModel.onEvent(CreateRoomUiEvent.SectionChanged(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Section")
                        },
                        shape = MaterialTheme.shapes.medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = viewModel.uiState.subject,
                        onValueChange = {
                            viewModel.onEvent(CreateRoomUiEvent.SubjectChanged(it))
                        },
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
                    Button(
                        onClick = {
                            viewModel.onEvent(CreateRoomUiEvent.OnCreateClick)
                        }
                    ) {
                        Text(text = "Create")
                    }
                }
            }
        }
    }
}