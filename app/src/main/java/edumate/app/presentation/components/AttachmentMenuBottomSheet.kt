package edumate.app.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.InsertLink
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentMenuBottomSheet(
    onDismissRequest: () -> Unit,
    openBottomSheet: Boolean,
    onInsertLinkClick: () -> Unit,
    onUploadFileClick: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (openBottomSheet) {
        val bottomMargin =
            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 10.dp

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
            windowInsets = WindowInsets(0)
        ) {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = Strings.insert_link))
                },
                modifier = Modifier.clickable {
                    onDismissRequest()
                    onInsertLinkClick()
                },
                leadingContent = {
                    Icon(imageVector = Icons.Outlined.InsertLink, contentDescription = null)
                }
            )
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = Strings.upload_file))
                },
                modifier = Modifier.clickable {
                    onDismissRequest()
                    onUploadFileClick()
                },
                leadingContent = {
                    Icon(imageVector = Icons.Outlined.UploadFile, contentDescription = null)
                }
            )
            Spacer(modifier = Modifier.height(bottomMargin))
        }
    }
}