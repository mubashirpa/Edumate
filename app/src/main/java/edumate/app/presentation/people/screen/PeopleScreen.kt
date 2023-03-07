package edumate.app.presentation.people.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edumate.app.presentation.people.PeopleFilter
import edumate.app.presentation.people.PeopleUiEvent
import edumate.app.presentation.people.PeopleViewModel
import edumate.app.presentation.people.screen.components.PeopleListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleScreen(
    viewModel: PeopleViewModel = hiltViewModel()
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        content = {
            item {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    FilterChip(
                        selected = viewModel.uiState.showAll,
                        onClick = {
                            viewModel.onEvent(PeopleUiEvent.OnFilterChange(PeopleFilter.ALL))
                        },
                        label = { Text("All") },
                        leadingIcon = if (viewModel.uiState.showAll) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = viewModel.uiState.showTeachers,
                        onClick = {
                            viewModel.onEvent(PeopleUiEvent.OnFilterChange(PeopleFilter.TEACHERS))
                        },
                        label = { Text("Teachers") },
                        leadingIcon = if (viewModel.uiState.showTeachers) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = viewModel.uiState.showStudents,
                        onClick = {
                            viewModel.onEvent(PeopleUiEvent.OnFilterChange(PeopleFilter.STUDENTS))
                        },
                        label = { Text("Students") },
                        leadingIcon = if (viewModel.uiState.showStudents) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
            items(viewModel.uiState.teachers) { teacher ->
                AnimatedVisibility(visible = !viewModel.uiState.showStudents) {
                    PeopleListItem(user = teacher)
                }
            }
            items(viewModel.uiState.students) { student ->
                AnimatedVisibility(visible = !viewModel.uiState.showTeachers) {
                    PeopleListItem(user = student)
                }
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