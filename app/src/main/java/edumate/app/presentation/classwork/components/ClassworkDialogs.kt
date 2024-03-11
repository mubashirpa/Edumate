package edumate.app.presentation.classwork.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
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
import edumate.app.domain.model.classroom.courseWork.CourseWorkType
import kotlinx.coroutines.launch
import edumate.app.R.string as Strings

@Composable
fun DeleteCourseWorkDialog(
    onDismissRequest: () -> Unit,
    open: Boolean,
    workType: CourseWorkType?,
    onConfirmButtonClick: () -> Unit,
) {
    if (open) {
        val title: String
        val message: String

        when (workType) {
            CourseWorkType.SHORT_ANSWER_QUESTION -> {
                title = stringResource(id = Strings.dialog_title_delete_question)
                message = stringResource(id = Strings.dialog_message_delete_coursework)
            }

            CourseWorkType.MULTIPLE_CHOICE_QUESTION -> {
                title = stringResource(id = Strings.dialog_title_delete_question)
                message = stringResource(id = Strings.dialog_message_delete_coursework)
            }

            null -> {
                // If workType is null, it indicates that the item is a Material.
                title = stringResource(id = Strings.dialog_title_delete_material)
                message = ""
            }

            else -> {
                title = stringResource(id = Strings.dialog_title_delete_assignment)
                message = stringResource(id = Strings.dialog_message_delete_coursework)
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
                    Text(stringResource(id = Strings.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = Strings.cancel))
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCourseWorkBottomSheet(
    onDismissRequest: () -> Unit,
    show: Boolean,
    onCreateCourseWork: (workType: CourseWorkType) -> Unit,
    onCreateMaterial: () -> Unit,
) {
    if (show) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val coroutineScope = rememberCoroutineScope()
        val bottomMargin =
            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 10.dp
        val colors = ListItemDefaults.colors(containerColor = Color.Transparent)

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
            windowInsets = WindowInsets(0),
        ) {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = Strings.assignment))
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
                    Text(text = stringResource(id = Strings.question))
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
                    Text(text = stringResource(id = Strings.material))
                },
                modifier =
                    Modifier.clickable {
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onDismissRequest()
                                onCreateMaterial()
                            }
                        }
                    },
                leadingContent = {
                    Icon(imageVector = Icons.Outlined.Book, contentDescription = null)
                },
                colors = colors,
            )
            Spacer(modifier = Modifier.height(bottomMargin))
        }
    }
}
