package app.edumate.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.InsertLink
import androidx.compose.material.icons.outlined.InsertPhoto
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.edumate.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAttachmentBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onInsertLinkClick: () -> Unit,
    onUploadFileClick: () -> Unit,
    onPickPhotoClick: () -> Unit,
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
                    Text(text = stringResource(id = R.string.insert_link))
                },
                modifier =
                    Modifier.clickable {
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onDismissRequest()
                                onInsertLinkClick()
                            }
                        }
                    },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.InsertLink,
                        contentDescription = null,
                    )
                },
                colors = colors,
            )
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.upload_file))
                },
                modifier =
                    Modifier.clickable {
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onDismissRequest()
                                onUploadFileClick()
                            }
                        }
                    },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.UploadFile,
                        contentDescription = null,
                    )
                },
                colors = colors,
            )
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.pick_photo))
                },
                modifier =
                    Modifier.clickable {
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onDismissRequest()
                                onPickPhotoClick()
                            }
                        }
                    },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.InsertPhoto,
                        contentDescription = null,
                    )
                },
                colors = colors,
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
