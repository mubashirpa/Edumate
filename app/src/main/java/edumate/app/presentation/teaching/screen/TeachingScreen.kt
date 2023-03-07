package edumate.app.presentation.teaching.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.teaching.TeachingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeachingScreen(
    viewModel: TeachingViewModel = hiltViewModel()
) {
    when {
        viewModel.uiState.loading -> {
            LoadingIndicator()
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = {
                    items(viewModel.uiState.classes) { room ->
                        Card(
                            onClick = { },
                            modifier = Modifier.aspectRatio(21f / 9f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = room.name,
                                            style = MaterialTheme.typography.headlineSmall
                                        )
                                        Text(
                                            text = room.section.orEmpty(),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                    IconButton(onClick = { }) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = null
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "0 students",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}