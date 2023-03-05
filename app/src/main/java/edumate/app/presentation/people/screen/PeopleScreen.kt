package edumate.app.presentation.people.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edumate.app.presentation.people.PeopleViewModel
import edumate.app.presentation.people.screen.components.PeopleListItem
import edumate.app.presentation.people.screen.components.TextAvatar

@Composable
fun PeopleScreen(
    viewModel: PeopleViewModel = hiltViewModel()
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        content = {
            item {
                Text(
                    text = "Teachers",
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            items(viewModel.uiState.teachers) { teacher ->
                PeopleListItem(user = teacher)
            }
            item {
                Text(
                    text = "Students",
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            items(viewModel.uiState.students) { student ->
                PeopleListItem(user = student)
            }
            val students = listOf("Student 1", "Student 2", "Student 3", "Student 4")
            items(students) {
                ListItem(
                    headlineText = {
                        Text(text = it)
                    },
                    leadingContent = {
                        TextAvatar(id = it, firstName = it, lastName = "")
                    }
                )
            }
        }
    )
}

@Composable
fun StreamScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Coming soon")
    }
}

@Composable
fun ClassworkScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Coming soon")
    }
}