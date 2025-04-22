package app.edumate.presentation.createCourseWork.components

import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Subject
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import app.edumate.R
import app.edumate.core.utils.FileType
import app.edumate.core.utils.FileUtils
import app.edumate.presentation.components.FieldListItem
import app.edumate.presentation.createCourseWork.CreateCourseWorkUiEvent
import app.edumate.presentation.createCourseWork.CreateCourseWorkUiState
import app.edumate.presentation.theme.EdumateTheme

@Composable
fun ContentMaterial(
    uiState: CreateCourseWorkUiState,
    onEvent: (CreateCourseWorkUiEvent) -> Unit,
    courseName: String,
    onNavigateToImageViewer: (url: String, title: String?) -> Unit,
    onNavigateToPdfViewer: (url: String, title: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val fileUtils = remember { FileUtils(context) }
    val itemModifier =
        Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraSmall,
            )

    Column(modifier = modifier) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            FieldListItem(
                headlineContent = {
                    OutlinedTextField(
                        state = uiState.title,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                        label = {
                            Text(text = stringResource(id = R.string.material_title))
                        },
                        supportingText =
                            uiState.titleError?.let { error ->
                                {
                                    Text(text = error.asString())
                                }
                            },
                        isError = uiState.titleError != null,
                        keyboardOptions =
                            KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrectEnabled = true,
                            ),
                    )
                },
                leadingIcon = Icons.Outlined.Book,
                trailingContent = {},
            )
            FieldListItem(
                headlineContent = {
                    Row(
                        modifier = itemModifier.horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        ElevatedSuggestionChip(
                            onClick = {},
                            label = {
                                Text(text = courseName)
                            },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ElevatedSuggestionChip(
                            onClick = {},
                            label = {
                                Text(text = stringResource(id = R.string.all_students))
                            },
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                },
                leadingIcon = Icons.Outlined.People,
                trailingContent = {},
            )
            FieldListItem(
                headlineContent = {
                    OutlinedTextField(
                        state = uiState.description,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = R.string.description))
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrectEnabled = true,
                            ),
                    )
                },
                leadingIcon = Icons.AutoMirrored.Outlined.Subject,
                trailingContent = {},
            )
            FieldListItem(
                headlineContent = {
                    Column(
                        modifier = itemModifier,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        uiState.attachments.onEachIndexed { index, material ->
                            AttachmentsListItem(
                                material = material,
                                fileUtils = fileUtils,
                                onClickFile = { mimeType, url, title ->
                                    when (mimeType) {
                                        FileType.IMAGE -> {
                                            onNavigateToImageViewer(url, title)
                                        }

                                        FileType.PDF -> {
                                            onNavigateToPdfViewer(url, title)
                                        }

                                        else -> {
                                            val browserIntent =
                                                Intent(
                                                    Intent.ACTION_VIEW,
                                                    url.toUri(),
                                                )
                                            context.startActivity(browserIntent)
                                        }
                                    }
                                },
                                onClickLink = { url ->
                                    val browserIntent =
                                        Intent(Intent.ACTION_VIEW, url.toUri())
                                    context.startActivity(browserIntent)
                                },
                                onRemoveClick = {
                                    onEvent(CreateCourseWorkUiEvent.RemoveAttachment(index))
                                },
                            )
                            HorizontalDivider()
                        }
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(id = R.string.add_attachment),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            },
                            modifier =
                                Modifier.clickable {
                                    onEvent(
                                        CreateCourseWorkUiEvent.OnShowAddAttachmentBottomSheetChange(
                                            true,
                                        ),
                                    )
                                },
                        )
                    }
                },
                leadingIcon = Icons.Outlined.Attachment,
                trailingContent = {},
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                onEvent(CreateCourseWorkUiEvent.CreateCourseWork)
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
        ) {
            val text =
                if (uiState.isNewCourseWork) {
                    stringResource(id = R.string.post)
                } else {
                    stringResource(id = R.string.save)
                }
            Text(text = text)
        }
        Spacer(modifier = Modifier.height(12.dp))
    }

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
}

@Preview(showBackground = true)
@Composable
private fun ContentMaterialPreview() {
    EdumateTheme {
        ContentMaterial(
            uiState = CreateCourseWorkUiState(),
            onEvent = {},
            courseName = "Course",
            onNavigateToImageViewer = { _, _ -> },
            onNavigateToPdfViewer = { _, _ -> },
        )
    }
}
