package edumate.app.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.InsertLink
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAttachmentBottomSheet(
    onDismissRequest: () -> Unit,
    showBottomSheet: Boolean,
    onInsertLinkClick: () -> Unit,
    onUploadFileClick: () -> Unit,
) {
    if (showBottomSheet) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val bottomMargin =
            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 10.dp

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
            windowInsets = WindowInsets(0),
        ) {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = Strings.insert_link))
                },
                modifier =
                    Modifier.clickable {
                        onDismissRequest()
                        onInsertLinkClick()
                    },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.InsertLink,
                        contentDescription = null,
                    )
                },
            )
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = Strings.upload_file))
                },
                modifier =
                    Modifier.clickable {
                        onDismissRequest()
                        onUploadFileClick()
                    },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.UploadFile,
                        contentDescription = null,
                    )
                },
            )
            Spacer(modifier = Modifier.height(bottomMargin))
        }
    }
}