package edumate.app.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import edumate.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseBottomSheet(
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    innerPadding: PaddingValues,
    onCreateClass: () -> Unit,
    onJoinClass: () -> Unit,
) {
    if (showBottomSheet) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
            windowInsets = WindowInsets(0),
        ) {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.create_class))
                },
                modifier =
                    Modifier.clickable {
                        onDismissRequest()
                        onCreateClass()
                    },
            )
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.join_class))
                },
                modifier =
                    Modifier.clickable {
                        onDismissRequest()
                        onJoinClass()
                    },
            )
            Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
        }
    }
}
