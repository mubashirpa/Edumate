package edumate.app.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
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
import edumate.app.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    innerPadding: PaddingValues,
    onCreateClass: () -> Unit,
    onJoinClass: () -> Unit,
) {
    if (show) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val coroutineScope = rememberCoroutineScope()
        val bottomMargin = innerPadding.calculateBottomPadding() + 10.dp
        val colors = ListItemDefaults.colors(containerColor = Color.Transparent)

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
            windowInsets = WindowInsets(0),
        ) {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.join_class))
                },
                modifier =
                    Modifier.clickable {
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onDismissRequest()
                                onJoinClass()
                            }
                        }
                    },
                colors = colors,
            )
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.create_class))
                },
                modifier =
                    Modifier.clickable {
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onDismissRequest()
                                onCreateClass()
                            }
                        }
                    },
                colors = colors,
            )
            Spacer(modifier = Modifier.height(bottomMargin))
        }
    }
}
