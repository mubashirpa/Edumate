package app.edumate.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.presentation.theme.EdumateTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onCreateClass: () -> Unit,
    onJoinClass: () -> Unit,
) {
    if (show) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val coroutineScope = rememberCoroutineScope()

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
        ) {
            AddCourseBottomSheetContent(
                onCreateClass = {
                    coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            onDismissRequest()
                            onCreateClass()
                        }
                    }
                },
                onJoinClass = {
                    coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            onDismissRequest()
                            onJoinClass()
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun AddCourseBottomSheetContent(
    onCreateClass: () -> Unit,
    onJoinClass: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ListItemDefaults.colors(containerColor = Color.Transparent)

    Column(modifier = modifier.padding(vertical = 12.dp)) {
        ListItem(
            headlineContent = {
                Text(text = stringResource(id = R.string.join_class))
            },
            modifier = Modifier.clickable(onClick = onJoinClass),
            colors = colors,
        )
        ListItem(
            headlineContent = {
                Text(text = stringResource(id = R.string.create_class))
            },
            modifier = Modifier.clickable(onClick = onCreateClass),
            colors = colors,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddCourseBottomSheetPreview() {
    EdumateTheme {
        AddCourseBottomSheetContent(
            onCreateClass = {},
            onJoinClass = {},
        )
    }
}
