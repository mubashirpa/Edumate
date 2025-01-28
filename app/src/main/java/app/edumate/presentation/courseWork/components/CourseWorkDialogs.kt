package app.edumate.presentation.courseWork.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.LiveHelp
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.domain.model.courseWork.CourseWorkType
import kotlinx.coroutines.launch

@Composable
fun DeleteCourseWorkDialog(
    workType: CourseWorkType?,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
) {
    if (workType != null) {
        val title: String
        val message: String

        when (workType) {
            CourseWorkType.ASSIGNMENT -> {
                title = stringResource(id = R.string.dialog_title_delete_assignment)
                message = stringResource(id = R.string.dialog_message_delete_coursework)
            }

            CourseWorkType.MATERIAL -> {
                title = stringResource(id = R.string.dialog_title_delete_material)
                message = stringResource(id = R.string.dialog_message_delete_material)
            }

            CourseWorkType.MULTIPLE_CHOICE_QUESTION -> {
                title = stringResource(id = R.string.dialog_title_delete_question)
                message = stringResource(id = R.string.dialog_message_delete_coursework)
            }

            CourseWorkType.SHORT_ANSWER_QUESTION -> {
                title = stringResource(id = R.string.dialog_title_delete_question)
                message = stringResource(id = R.string.dialog_message_delete_coursework)
            }
        }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = title)
            },
            text = {
                Text(text = message)
            },
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) {
                    Text(stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCourseWorkBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onCreateCourseWork: (workType: CourseWorkType) -> Unit,
) {
    if (show) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val coroutineScope = rememberCoroutineScope()
        val colors = ListItemDefaults.colors(containerColor = Color.Transparent)

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
        ) {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.assignment))
                },
                modifier =
                    Modifier.clickable {
                        onDismissRequest()
                        onCreateCourseWork(CourseWorkType.ASSIGNMENT)
                    },
                leadingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Assignment,
                        contentDescription = null,
                    )
                },
                colors = colors,
            )
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.question))
                },
                modifier =
                    Modifier.clickable {
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onDismissRequest()
                                onCreateCourseWork(CourseWorkType.SHORT_ANSWER_QUESTION)
                            }
                        }
                    },
                leadingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.LiveHelp,
                        contentDescription = null,
                    )
                },
                colors = colors,
            )
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.material))
                },
                modifier =
                    Modifier.clickable {
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onDismissRequest()
                                onCreateCourseWork(CourseWorkType.MATERIAL)
                            }
                        }
                    },
                leadingContent = {
                    Icon(imageVector = Icons.Outlined.Book, contentDescription = null)
                },
                colors = colors,
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
