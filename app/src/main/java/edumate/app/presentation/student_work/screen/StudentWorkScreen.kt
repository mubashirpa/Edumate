package edumate.app.presentation.student_work.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edumate.app.presentation.student_work.StudentWorkViewModel

@Composable
fun StudentWorkScreen(
    viewModel: StudentWorkViewModel = hiltViewModel()
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 10.dp),
        content = {
            items(viewModel.uiState.studentSubmissions) { studentSubmission ->
                ListItem(
                    headlineContent = {
                        Text(text = studentSubmission.userId)
                    },
                    leadingContent = {
                        Checkbox(checked = false, onCheckedChange = {})
                    }
                )
            }
        }
    )
}