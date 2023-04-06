package edumate.app.presentation.view_classwork.screen

import android.text.format.DateUtils
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import edumate.app.R.string as Strings
import edumate.app.core.ext.header
import edumate.app.core.utils.FileType
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.presentation.components.ComingSoon
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.view_classwork.ViewClassworkUiState
import edumate.app.presentation.view_classwork.screen.components.AttachmentsListItem
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewClassworkScreen(
    uiState: ViewClassworkUiState,
    classworkType: CourseWorkType,
    onBackPressed: () -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = Strings.navigate_up)
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
        when (classworkType) {
            CourseWorkType.MATERIAL -> {
                ContentMaterial(uiState = uiState)
            }
            CourseWorkType.ASSIGNMENT -> {
                ContentAssignment(uiState = uiState)
            }
            else -> {
                ComingSoon()
            }
        }
    }
}

@Composable
fun ContentAssignment(
    uiState: ViewClassworkUiState
) {
    if (uiState.error == null) {
        val context = LocalContext.current
        val bottomMargin = WindowInsets.navigationBars.asPaddingValues()
            .calculateBottomPadding() + 10.dp
        val contentPadding = PaddingValues(
            start = 16.dp,
            top = 10.dp,
            end = 16.dp,
            bottom = bottomMargin
        )
        val fileUtils = remember { FileUtils(context) }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            content = {
                header {
                    Column {
                        val dueDate = uiState.classwork.dueTime
                        if (dueDate != null) {
                            val date = DateUtils.getRelativeTimeSpanString(dueDate.time)
                            Text(
                                text = stringResource(
                                    id = Strings.due_,
                                    date.toString()
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = uiState.classwork.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        val points = uiState.classwork.maxPoints
                        if (points != null && points > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$points points",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                val description = uiState.classwork.description
                if (description != null) {
                    header {
                        Text(
                            text = description,
                            modifier = Modifier.padding(top = 2.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                val attachments = uiState.classwork.materials
                if (attachments.isNotEmpty()) {
                    header {
                        Text(
                            text = "Attachments",
                            modifier = Modifier.padding(top = 6.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    items(attachments) {
                        when {
                            it.link != null -> {
                                AttachmentsListItem(
                                    title = it.link.title ?: it.link.url,
                                    icon = Icons.Default.Link,
                                    onClick = {}
                                )
                            }
                            it.file != null -> {
                                val icon = when (fileUtils.getFileType(it.file.type)) {
                                    FileType.IMAGE -> Icons.Default.Image
                                    FileType.VIDEO -> Icons.Default.VideoFile
                                    FileType.AUDIO -> Icons.Default.AudioFile
                                    FileType.PDF -> Icons.Default.PictureAsPdf
                                    FileType.UNKNOWN -> Icons.Default.InsertDriveFile
                                }
                                AttachmentsListItem(
                                    title = it.file.title ?: it.file.url,
                                    file = it.file,
                                    icon = icon,
                                    onClick = {}
                                )
                            }
                        }
                    }
                }
            }
        )
    } else {
        ErrorScreen(errorMessage = uiState.error.asString())
    }
}

@Composable
fun ContentMaterial(
    uiState: ViewClassworkUiState
) {
    if (uiState.error == null) {
        val context = LocalContext.current
        val bottomMargin = WindowInsets.navigationBars.asPaddingValues()
            .calculateBottomPadding() + 10.dp
        val contentPadding = PaddingValues(bottom = bottomMargin)
        val fileUtils = remember {
            FileUtils(context)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            content = {
                item {
                    val dueDate = uiState.classwork.dueTime
                    ListItem(
                        headlineContent = {
                            Text(
                                text = uiState.classwork.title,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        },
                        supportingContent = if (dueDate != null) {
                            {
                                val date = DateUtils.getRelativeTimeSpanString(
                                    dueDate.time
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(
                                        id = Strings.due_,
                                        date.toString()
                                    ),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        } else {
                            null
                        }
                    )
                    val description = uiState.classwork.description
                    if (description != null) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        )
                    }
                }
                val attachments = uiState.classwork.materials
                if (attachments.isNotEmpty()) {
                    item {
                        Text(
                            text = "Attachments",
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                        FlowRow(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            mainAxisSize = SizeMode.Expand,
                            mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                            mainAxisSpacing = 10.dp,
                            crossAxisSpacing = 10.dp
                        ) {
                            attachments.forEach {
                                when {
                                    it.link != null -> {
                                        AttachmentsListItem(
                                            title = it.link.title ?: it.link.url,
                                            icon = Icons.Default.Link,
                                            onClick = {}
                                        )
                                    }
                                    it.file != null -> {
                                        val icon = when (fileUtils.getFileType(it.file.type)) {
                                            FileType.IMAGE -> Icons.Default.Image
                                            FileType.VIDEO -> Icons.Default.VideoFile
                                            FileType.AUDIO -> Icons.Default.AudioFile
                                            FileType.PDF -> Icons.Default.PictureAsPdf
                                            FileType.UNKNOWN -> Icons.Default.InsertDriveFile
                                        }
                                        AttachmentsListItem(
                                            title = it.file.title ?: it.file.url,
                                            icon = icon,
                                            onClick = {}
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    } else {
        ErrorScreen(errorMessage = uiState.error.asString())
    }
}