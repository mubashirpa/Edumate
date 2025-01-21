package app.edumate.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.presentation.theme.EdumateTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCourseBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onCreateClass: () -> Unit,
) {
    if (show) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val coroutineScope = rememberCoroutineScope()
        val nameState = rememberTextFieldState()
        val sectionState = rememberTextFieldState()
        val subjectState = rememberTextFieldState()
        val roomState = rememberTextFieldState()

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
        ) {
            CreateCourseBottomSheetContent(
                nameState = nameState,
                sectionState = sectionState,
                subjectState = subjectState,
                roomState = roomState,
                onCreateClass = {
                    coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            onDismissRequest()
                            onCreateClass()
                        }
                    }
                },
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateCourseBottomSheetContent(
    nameState: TextFieldState,
    sectionState: TextFieldState,
    subjectState: TextFieldState,
    roomState: TextFieldState,
    onCreateClass: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val maxWidthModifier = Modifier.fillMaxWidth()

    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(text = stringResource(R.string.create_class_description))
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            state = nameState,
            modifier = maxWidthModifier,
            label = {
                Text(text = stringResource(R.string.class_name))
            },
            lineLimits = TextFieldLineLimits.SingleLine,
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            state = sectionState,
            modifier = maxWidthModifier,
            label = {
                Text(text = stringResource(R.string.section))
            },
            lineLimits = TextFieldLineLimits.SingleLine,
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            state = subjectState,
            modifier = maxWidthModifier,
            label = {
                Text(text = stringResource(R.string.subject))
            },
            lineLimits = TextFieldLineLimits.SingleLine,
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            state = roomState,
            modifier = maxWidthModifier,
            label = {
                Text(text = stringResource(R.string.room))
            },
            lineLimits = TextFieldLineLimits.SingleLine,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onCreateClass,
            modifier = maxWidthModifier,
        ) {
            Text(text = stringResource(R.string.create))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateCourseBottomSheetPreview() {
    EdumateTheme {
        CreateCourseBottomSheetContent(
            nameState = rememberTextFieldState(),
            sectionState = rememberTextFieldState(),
            subjectState = rememberTextFieldState(),
            roomState = rememberTextFieldState(),
            onCreateClass = {},
        )
    }
}
