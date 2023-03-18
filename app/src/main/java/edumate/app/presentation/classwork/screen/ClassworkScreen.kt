package edumate.app.presentation.classwork.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.LiveHelp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.classwork.ClassworkUiEvent
import edumate.app.presentation.classwork.ClassworkViewModel
import edumate.app.presentation.classwork.DataState
import edumate.app.presentation.classwork.screen.components.ClassworkListItem
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassworkScreen(
    viewModel: ClassworkViewModel = hiltViewModel(),
    userType: UserType,
    workType: CourseWorkType,
    navigateToCreateClasswork: (workType: String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        when (viewModel.uiState.dataState) {
            DataState.UNKNOWN -> {
                // Nothing happened
            }
            DataState.LOADING -> {
                LoadingIndicator()
            }
            DataState.ERROR -> {
                ErrorScreen(
                    errorMessage = viewModel.uiState.errorMessage.asString(),
                    onRetry = {
                        // TODO("Not yet implemented")
                    }
                )
            }
            DataState.EMPTY -> {
                ErrorScreen(
                    errorMessage = if (userType == UserType.TEACHER) {
                        "Add assignments and other works for the class"
                    } else {
                        "Your teacher hasn't assigned any classwork yet"
                    }
                )
            }
            DataState.SUCCESS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    content = {
                        items(viewModel.uiState.classWorks) { classWork ->
                            ClassworkListItem(
                                onClick = {
                                    // TODO("Not yet implemented")
                                },
                                userType = userType,
                                workType = workType,
                                work = classWork
                            )
                        }
                    }
                )
            }
        }

        if (userType == UserType.TEACHER) {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(ClassworkUiEvent.OnOpenFabMenuChange(true))
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .imePadding()
                    .padding(16.dp)
            ) { Icon(imageVector = Icons.Default.Add, contentDescription = "Create classwork") }
        }
    }

    if (viewModel.uiState.openFabMenu) {
        // TODO("Fix alignment")
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
            }
        ) {
            ListItem(
                headlineContent = { Text(text = "Assignment") },
                modifier = Modifier.clickable {
                    viewModel.onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
                    navigateToCreateClasswork(CourseWorkType.ASSIGNMENT.toString())
                },
                leadingContent = {
                    Icon(imageVector = Icons.Outlined.Assignment, contentDescription = null)
                }
            )
            ListItem(
                headlineContent = { Text(text = "Question") },
                modifier = Modifier.clickable {
                    viewModel.onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
                    navigateToCreateClasswork(CourseWorkType.ASSIGNMENT.toString())
                },
                leadingContent = {
                    Icon(imageVector = Icons.Outlined.LiveHelp, contentDescription = null)
                }
            )
            ListItem(
                headlineContent = { Text(text = "Material") },
                modifier = Modifier.clickable {
                    viewModel.onEvent(ClassworkUiEvent.OnOpenFabMenuChange(false))
                    navigateToCreateClasswork(CourseWorkType.ASSIGNMENT.toString())
                },
                leadingContent = {
                    Icon(imageVector = Icons.Outlined.Book, contentDescription = null)
                }
            )
            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }
    }
}